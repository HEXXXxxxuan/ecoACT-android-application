package com.go4.application.historical.MovieDemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go4.application.R;

import java.util.Map;

/*
 *  This LayoutActivity accepts the passed data parameters
 *  and displays them directly on the window control
 */
public class DetailsActivity extends AppCompatActivity {

    private Map<String, String> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setTitle("Movie Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        dataMap = (Map<String, String>) intent.getSerializableExtra("movie_data");

        // Get View and display it directly
        ((TextView)findViewById(R.id.tv_title)).setText(dataMap.get("title"));
        ((TextView)findViewById(R.id.tv_genres)).setText(dataMap.get("genres"));
        ((TextView)findViewById(R.id.tv_year)).setText(dataMap.get("year"));
        ((TextView)findViewById(R.id.tv_average_rating)).setText(dataMap.get("averageRating"));
        ((TextView)findViewById(R.id.tv_male_average_rating)).setText(dataMap.get("maleAverageRating"));
        ((TextView)findViewById(R.id.tv_male_count)).setText(dataMap.get("maleCount"));
        ((TextView)findViewById(R.id.tv_female_average_rating)).setText(dataMap.get("femaleAverageRating"));
        ((TextView)findViewById(R.id.tv_female_count)).setText(dataMap.get("femaleCount"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}