package com.go4.application;

import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout cardList;
    private Spinner suburbSpinner;
    private Button addButton;
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
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("userName");
        TextView username = findViewById(R.id.pa_username);
        username.setText(usernameString);
    }

    private void addSuburb() {
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        addCard("Label (e.g. Home/Work/School)", selectedSuburb, "good", 69);
    }

    private void addCard(String location, String suburb, String quality, Integer number) {
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