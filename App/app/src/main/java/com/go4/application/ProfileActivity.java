package com.go4.application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout cardList;

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
        inflater = getLayoutInflater();
        addCard("a", "b", "Good", 10);
        addCard("b", "c", "Bad", 11);
        addCard("c", "d", "Medium", 12);
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
}