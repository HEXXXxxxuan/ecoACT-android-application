package com.go4.application;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;
import com.go4.utils.CsvParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
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
    private List<SuburbCard> pinnedSuburbs;
    private Handler handler;
    private Runnable updateRunnable;

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

        // Display User Name
        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("displayName");
        TextView username = findViewById(R.id.pa_username);
        username.setText(usernameString);

        // Create AVL Tree
        createAVLTree();

        // Pinned Suburb Card features
        cardList = findViewById(R.id.pa_cardList);
        suburbSpinner = findViewById(R.id.pa_suburb_spinner);
        inflater = getLayoutInflater();
        Button addButton = findViewById(R.id.pa_add_button);
        addButton.setOnClickListener(v -> addButtonOnClick());
        suburbSpinner();

        // Upload Profile Picture
        imageView = findViewById(R.id.pa_profile);
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

        imageView.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build()));

        readProfilePicture();
    }

    @Override
    protected void onStart() {
        super.onStart();

        pinnedSuburbs = new ArrayList<SuburbCard>();
        readPinnedSuburbs();

        // Update suburb cards every 15 minutes
        updateRunnable = new Runnable() {
            @Override public void run() {
                updateSuburbCards();
                handler.postDelayed(this, 15 * 60 * 1000);
            }
        };
    }

    private String getFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String filename = "profile_picture.jpg";
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        return String.format("%s/%s", directory, filename);
    }

    private void readProfilePicture() {
        String filePath = getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        } else {
            imageView.setImageResource(R.drawable.default_profile);
        }
    }

    public void writeProfilePicture(Bitmap bitmapImage) {
        String filePath = getFilePath();
        File file = new File(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSuburbCards() {
        for (SuburbCard card: pinnedSuburbs) {
            String suburb = card.getSuburb();
            String quality = "";
            double pm10Number = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
            String currentDateAndTime = sdf.format(new Date());
            String key = suburb + "_" + currentDateAndTime;
            AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

            if (record != null) {
                pm10Number = record.getPm10();
                int aqi = (int) Math.round(record.getAqi());
                if (aqi <= 3) {
                    quality = "Good"; // Green
                } else if (aqi <= 6) {
                    quality = "Moderate"; // Yellow
                } else {
                    quality = "Bad";// Red
                }
            }

            card.setQuality(quality);
            card.setPm10Number(String.valueOf(pm10Number));
        }
        writePinnedSuburbs();
    }

    private void addButtonOnClick() {
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        String currentDateAndTime = sdf.format(new Date());
        String key = selectedSuburb + "_" + currentDateAndTime;

        double pm10Number = 0;
        String quality = "N/A";
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);
        if (record != null) {
            pm10Number = record.getPm10();
            int aqi = (int) Math.round(record.getAqi());
            if (aqi <= 3) {
                quality = "Good"; // Green
            } else if (aqi <= 6) {
                quality = "Moderate"; // Yellow
            } else {
                quality = "Bad";// Red
            }
        } else {
            Toast.makeText(this, "No matching records.", Toast.LENGTH_SHORT).show();
        }

        String label = "Label (e.g. Home/Work/School)";
        addSuburbCard(label, selectedSuburb, quality, String.valueOf(pm10Number));
        writePinnedSuburbs();
    }

    private void readPinnedSuburbs() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String filename = "pinned_suburbs.txt";
        File directory = cw.getDir("text", Context.MODE_PRIVATE);
        String filePath = String.format("%s/%s", directory, filename);
        File path = new File(filePath);

        StringBuilder fileContent = new StringBuilder();

        try {
            // Read the content of the file
            FileInputStream fileInputStream = new FileInputStream(path);
            int a;
            while ((a = fileInputStream.read()) != -1) {
                fileContent.append((char) a);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] lines = fileContent.toString().split("\n");
        for (String line : lines) {
            String[] lineParts = line.split(",");
            if (lineParts.length == 4) {
                addSuburbCard(lineParts[0], lineParts[1], lineParts[2], lineParts[3]);
            }
        }
    }

    private void writePinnedSuburbs() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String filename = "pinned_suburbs.txt";
        File directory = cw.getDir("text", Context.MODE_PRIVATE);
        String filePath = String.format("%s/%s", directory, filename);
        File path = new File(filePath);
        try {
            // Write the input text to the file
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            StringBuilder data = new StringBuilder();
            for (SuburbCard card: pinnedSuburbs) {
                String line = card.getData();
                data.append(line);
            }
            fileOutputStream.write(data.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSuburbCard(String label, String suburb, String quality, String number) {
        SuburbCard card = new SuburbCard(label, suburb, quality, number);
        pinnedSuburbs.add(card);

        View cardView = inflater.inflate(R.layout.activity_profile_card, cardList, false);
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
        TextView suburbTextView = cardView.findViewById(R.id.pa_card_suburb);
        suburbTextView.setText(suburb);
        TextView qualityTextView = cardView.findViewById(R.id.pa_card_quality);
        qualityTextView.setText(quality);
        TextView numberTextView = cardView.findViewById(R.id.pa_card_number);
        numberTextView.setText(String.valueOf((number)));
        cardList.addView(cardView);
    }

    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
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