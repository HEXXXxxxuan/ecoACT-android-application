package com.go4.application;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go4.application.historical.AirQualityRecord;
import com.go4.application.tree.AVLTree;
import com.go4.utils.CsvParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout cardList;
    private Spinner suburbSpinner;
    private Button addButton;
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
        cardList = (LinearLayout) findViewById(R.id.pa_cardList);
        suburbSpinner = (Spinner) findViewById(R.id.pa_suburb_spinner);
        addButton = (Button) findViewById(R.id.pa_add_button);
        inflater = getLayoutInflater();

        addButton.setOnClickListener(v -> addSuburb());

        suburbSpinner();

        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("displayName");
        TextView username = findViewById(R.id.pa_username);
        username.setText(usernameString);

        createAVLTree();
    }

    @Override
    protected void onStart(){
        super.onStart();
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

        Toast.makeText(this, "key:" + key, Toast.LENGTH_SHORT).show();

        double pm10Number = 0;
        String quality = "N/A";

        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        if (record != null) {
            pm10Number = record.getPm10();

            // Update semiCircleArcProgressBar and AQI status

            int aqi = (int) Math.round(record.getAqi());
            if (aqi < 50) {
                quality = "good"; // Green
            } else if (aqi < 100) {
                quality = "medium"; // Yellow
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