package com.go4.application.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.go4.application.FirebaseLoginActivity;
import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;
import com.go4.utils.CsvParser;
import org.json.JSONException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.simple.parser.*;

public class ProfileActivity extends AppCompatActivity {
    private static AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;
    private static List<SuburbCard> pinnedSuburbs;
    private Handler handler;
    private FirebaseUser user;
    private ImageView profileImage;

    private final ActivityResultLauncher<Void> getUser = registerForActivityResult(
        new FirebaseLoginActivity.FirebaseLoginActivityResultContract(), result->{
            if(result != null){
                user = result;
            }
            else{
                Log.e("Login", "GUAH NO USER");
            }
        });

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        // Load the bitmap from the URI
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        writeProfilePicture(bitmapImage);
                        readProfilePicture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        createAVLTree();
        suburbSpinner();

        profileImage = findViewById(R.id.pa_profile);
        user = getIntent().getParcelableExtra("User", FirebaseUser.class);
        if(user==null){Log.e("Login", "Profile started with no User Goofy Ahh");finish();return;}
        ((TextView) findViewById(R.id.pa_username)).setText(user.getEmail());

        findViewById(R.id.pa_add_button).setOnClickListener(v -> addButtonOnClick());
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getUser.launch(null);
        });
        profileImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        findViewById(R.id.nav_suburb_live).setOnClickListener(v -> {
            Intent suburbLiveIntent = new Intent(ProfileActivity.this, SuburbLiveActivity.class);
            suburbLiveIntent.putExtra("displayName", user.getEmail());
            startActivity(suburbLiveIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        readPinnedSuburbs();
        handler = new Handler();

        // Update suburb cards every 15 minutes
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePinnedSuburbs();
                handler.postDelayed(this, 15 * 60 * 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private File getFile(String directory, String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        return new File(String.format("%s/%s", cw.getDir(directory, Context.MODE_PRIVATE), name));
    }

    private void readProfilePicture() {
        File file = getFile("images", "profile_picture.jpg");
        if (file.exists()) {
            profileImage.setImageDrawable(Drawable.createFromPath(String.valueOf(file.toPath())));
        } else {
            profileImage.setImageResource(R.drawable.default_profile);
        }
    }

    public void writeProfilePicture(Bitmap bitmapImage) {
        File file = getFile("Images", "profile_picture.jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] searchForQualityAndPm10Number(String suburb) {
        String[] result = new String[2];
        String quality = "N/A";
        double pm10Number = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        String currentDateAndTime = sdf.format(new Date());
        String key = suburb + "_" + currentDateAndTime;
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);
        if (record != null) {
            pm10Number = record.getPm10();
            double aqi = record.getAqi();
            if (aqi <= 3) {
                quality = "Good"; // Green
            } else if (aqi <= 6) {
                quality = "Moderate"; // Yellow
            } else {
                quality = "Bad";// Red
            }
        }
        result[0] = quality;
        result[1] = String.valueOf(pm10Number);
        return result;
    }

    /**
     * <H1> ENSURE TXT FILE IS SAFELY FORMATTED WITH 3 COMMAS PER LINE AND A BLANK AT THE END</H1>
     */
    private void readPinnedSuburbs() {
        pinnedSuburbs = new ArrayList<>();
        ((LinearLayout) findViewById(R.id.pa_cardList)).removeAllViews();
        try{
            Scanner s = new Scanner(getFile("text", "pinned_suburbs.txt"));
            while(s.hasNextLine()){
                String[] lineParts = s.nextLine().split(",");
                addSuburbCard(lineParts[0], lineParts[1], lineParts[2], lineParts[3]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writePinnedSuburbs() {
        try {
            FileWriter wr = new FileWriter(getFile("text", "pinned_suburbs.txt"));
            for (SuburbCard card: pinnedSuburbs) {
                wr.write(card.getData());
            }
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePinnedSuburbs() {
        for (SuburbCard card: pinnedSuburbs) {
            String[] result = searchForQualityAndPm10Number(card.getSuburb());
            card.setQuality(result[0]);
            card.setPm10Number(result[1]);
        }
        writePinnedSuburbs();
    }

    private void addButtonOnClick() {
        String selectedSuburb = ((Spinner) findViewById(R.id.suburbSpinner)).getSelectedItem().toString();
        String[] result = searchForQualityAndPm10Number(selectedSuburb);
        String quality = result[0];
        String pm10Number = result[1];
        String label = "Label (e.g. Home/Work/School)";
        addSuburbCard(label, selectedSuburb, quality, pm10Number);
        writePinnedSuburbs();
    }

    private void addSuburbCard(String label, String suburb, String quality, String number) {
        SuburbCard card = new SuburbCard(label, suburb, quality, number);
        pinnedSuburbs.add(card);

        View cardView = getLayoutInflater().inflate(R.layout.activity_profile_card, findViewById(R.id.pa_cardList), false);
        EditText labelTextView = cardView.findViewById(R.id.pa_card_label);
        labelTextView.setText(label);
        labelTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                card.setLabel(labelTextView.getText().toString());
                writePinnedSuburbs();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ((TextView) cardView.findViewById(R.id.pa_card_suburb)).setText(suburb);
        ((TextView) cardView.findViewById(R.id.pa_card_quality)).setText(quality);
        ((TextView) cardView.findViewById(R.id.pa_card_number)).setText(number);

        LinearLayout linearLayout = cardView.findViewById(R.id.pa_card);
        if (Objects.equals(quality, "Good")) {
            linearLayout.setBackgroundResource(R.drawable.rounded_bg_good);
            ImageView image = cardView.findViewById(R.id.pa_card_image);
            image.setImageResource(R.drawable.quality_good);
        } else if (Objects.equals(quality, "Moderate")) {
            linearLayout.setBackgroundResource(R.drawable.rounded_bg_moderate);
        } else if (Objects.equals(quality, "Bad")) {
            linearLayout.setBackgroundResource(R.drawable.rounded_bg_bad);
        }
        linearLayout.setOnLongClickListener(v -> {
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance(card);
            dialog.show(getSupportFragmentManager(), "DELETE_DIALOG");
            return true;
        });
        ((LinearLayout) findViewById(R.id.pa_cardList)).addView(cardView);
    }

    public class DeleteDialogFragment extends DialogFragment {
        private SuburbCard card;

        public DeleteDialogFragment newInstance(SuburbCard card) {
            DeleteDialogFragment fragment = new DeleteDialogFragment();
            fragment.card = card;
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage("Delete suburb card?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    pinnedSuburbs.remove(card);
                    writePinnedSuburbs();
                    readPinnedSuburbs();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }

    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    private void suburbSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loadSuburbsFromJson());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.suburbSpinner)).setAdapter(adapter);
    }

    private List<String> loadSuburbsFromJson(){
        List<String> suburbs = new ArrayList<>();
        try {


        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbs;

    }
}