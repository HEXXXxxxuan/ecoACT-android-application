package com.go4.application;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go4.application.historical.AirQualityRecord;
import com.go4.application.tree.AVLTree;
import com.go4.utils.CsvParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout cardList;
    private Spinner suburbSpinner;
    private ImageView imageView;
    private AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;
//    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("displayName");
        TextView username = findViewById(R.id.pa_username);
        username.setText(usernameString);

        createAVLTree();

        cardList = findViewById(R.id.pa_cardList);
        suburbSpinner = findViewById(R.id.pa_suburb_spinner);
        inflater = getLayoutInflater();
        Button addButton = findViewById(R.id.pa_add_button);
        addButton.setOnClickListener(v -> addSuburb());

        suburbSpinner();

        imageView = findViewById(R.id.pa_profile);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);

                        try {
                            // Load the bitmap from the URI
                            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            // Call the Save function
                            String savedPath = saveProfilePicture(bitmapImage);
                            Log.d("PhotoPicker", "Image saved at: " + savedPath);
                            loadProfilePicture();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("PhotoPicker", "Error loading image: " + e.getMessage());
                        }

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        imageView.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        loadProfilePicture();
    }

    public String saveProfilePicture(Bitmap bitmapImage) {
        String uniqueNamePath = generatePath(true);
        File path = new File(uniqueNamePath);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Filename", e.getMessage());
            return "";
        }

        return path.toString();
    }

    public String generatePath(Boolean returnFullPath) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String filename = "profile_picture.jpg";
        if (returnFullPath) {
            File directory = cw.getDir("images", Context.MODE_PRIVATE);
            return String.format("%s/%s", directory, filename);
        } else {
            return filename;
        }
    }

    public void loadProfilePicture() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, "profile_picture" + ".jpg");

        if (file.exists()) {
            imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        } else {
            imageView.setImageResource(R.drawable.default_profile);
        }
    }

    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    private void addSuburb() {
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        String currentDateAndTime = sdf.format(new Date());

        String key = selectedSuburb + "_" + currentDateAndTime;

        double pm10Number = 0;
        String quality = "N/A";

        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        if (record != null) {
            pm10Number = record.getPm10();

            // Update semiCircleArcProgressBar and AQI status

            int aqi = (int) Math.round(record.getAqi());
            if (aqi <= 3) {
                quality = "good"; // Green
            } else if (aqi <= 6) {
                quality = "moderate"; // Yellow
            } else {
                quality = "bad";// Red
            }
        } else {
            Toast.makeText(this, "No matching records.", Toast.LENGTH_SHORT).show();
        }

        addCard("Label (e.g. Home/Work/School)", selectedSuburb, quality, pm10Number);
    }

    private void addCard(String location, String suburb, String quality, double number) {
        View card = inflater.inflate(R.layout.activity_profile_card, cardList, false);
        TextView locationTextView = card.findViewById(R.id.pa_card_location);
        locationTextView.setText(location);

        TextView suburbTextView = card.findViewById(R.id.pa_card_suburb);
        suburbTextView.setText(suburb);

        TextView qualityTextView = card.findViewById(R.id.pa_card_quality);
        qualityTextView.setText(quality);

        TextView numberTextView = card.findViewById(R.id.pa_card_number);
        numberTextView.setText(String.valueOf((number)));
        cardList.addView(card);
    }

    private void suburbSpinner() {
        List<String> suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
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