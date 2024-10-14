package com.go4.application.profile;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.go4.application.FirebaseLoginActivity;
import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;
import com.go4.utils.CsvParser;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.android.material.snackbar.Snackbar;
import java.util.Scanner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.simple.parser.*;


/**
 * Represents the graphical profile page additionally containing logic.
 */
public class ProfileActivity extends AppCompatActivity {
    private static AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;
    private ArrayList<SuburbCard> pinnedSuburbs;
    private FirebaseUser user;
    private ImageView profileImage;
    private SuburbCardViewAdapter recyclerViewAdapter;
    private RecyclerView suburbCardList;

    /**
     * Launches the user activity to retrieve the present user.
     */
    private final ActivityResultLauncher<Void> getUser = registerForActivityResult(
        new FirebaseLoginActivity.FirebaseLoginActivityResultContract(), result->{
            if(result != null){
                user = result;
            }
            else{
                Log.e("Login", "GUAH NO USER");
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // If somehow this error occurs
        user = getIntent().getParcelableExtra("User", FirebaseUser.class);
        if(user==null){
            Log.e("Login", "Profile started with no User Goofy Ahh");
            finish();
            return;
        }

        createAVLTree();
        suburbSpinner();

        // Logout
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getUser.launch(null);
        });

        // Load and Save Profile Picture
        profileImage = findViewById(R.id.pa_profile);
        editableProfilePicture();

        // Display Pinned Suburb Cards
        suburbCardList = findViewById(R.id.pa_cardList);
        displayPinnedSuburbCards();

        // setText and onClickListeners
        ((TextView) findViewById(R.id.pa_username)).setText(user.getEmail());

        // Set up ADD pinned suburb button
        findViewById(R.id.pa_add_button).setOnClickListener(v -> addButtonOnClick());

        // Nav Bar
        LinearLayout profile = findViewById(R.id.nav_profile);
        LinearLayout suburb_live = findViewById(R.id.nav_suburb_live);

        findViewById(R.id.nav_suburb_live).setOnClickListener(v -> {
            Intent suburbLiveIntent = new Intent(ProfileActivity.this, SuburbLiveActivity.class);
            suburbLiveIntent.putExtra("displayName", user.getEmail());
            startActivity(suburbLiveIntent);
        });
    }

    /**
     * Calls {@link #updatePinnedSuburbs()} every 15 minutes
     */
    @Override
    protected void onStart() {
        super.onStart();
        Handler handler = new Handler();
        Runnable updateRunnable = new Runnable() {
            @Override public void run() {
                updatePinnedSuburbs();
                handler.postDelayed(this, 15 * 60 * 1000);
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        writePinnedSuburbs();
    }

    @Override
    protected void onStop() {
        writePinnedSuburbs();
        super.onStop();
    }

    /**
     * Load and Save Profile Picture from internal storage
     * <p>With reference to <a href="https://developer.android.com/training/data-storage/shared/photopicker">this website</a></p>
     */
    private void editableProfilePicture() {
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try { // Load the bitmap from the URI
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
        profileImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        readProfilePicture();
    }

    /**
     * Display Suburb Cards using {@link SuburbCardViewAdapter} and {@link SuburbCard}
     * <p>Swipe right on suburb card to delete it, and press undo button to undo.
     * With reference to <a href="https://www.geeksforgeeks.org/how-to-add-dividers-in-android-recyclerview/">this website</a></p>
     */
    private void displayPinnedSuburbCards() {
        pinnedSuburbs = new ArrayList<>();
        readPinnedSuburbs();
        recyclerViewAdapter = new SuburbCardViewAdapter(pinnedSuburbs);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        suburbCardList.setLayoutManager(manager);
        suburbCardList.setAdapter(recyclerViewAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition();
                SuburbCard deletedSuburbCard = pinnedSuburbs.remove(index);
                recyclerViewAdapter.notifyItemRemoved(index);
                writePinnedSuburbs();

                Snackbar.make(suburbCardList, deletedSuburbCard.getLabel(), Snackbar.LENGTH_LONG).setAction("Undo", v -> {
                    pinnedSuburbs.add(index, deletedSuburbCard);
                    recyclerViewAdapter.notifyItemInserted(index);
                    writePinnedSuburbs();
                }).show();
            }
        }).attachToRecyclerView(suburbCardList);
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

    private String[] searchForQualityAndPm10Number(String suburb) {
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
        String label = "Label (e.g. Home/Work/School)";
        addSuburbCard(label, selectedSuburb, result[0], result[1]);
        writePinnedSuburbs();
    }

    private void addSuburbCard(String label, String suburb, String quality, String number) {
        SuburbCard card = new SuburbCard(label, suburb, quality, number);
        pinnedSuburbs.add(card);
        suburbCardList.setAdapter(recyclerViewAdapter);
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
            InputStream is = getAssets().open("canberra_suburbs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                suburbs.add(jsonArray.getString(i));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbs;
    }
}