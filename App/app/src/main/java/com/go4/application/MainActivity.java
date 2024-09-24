package com.go4.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.go4.application.Controller.DataController;
import com.go4.application.adapter.LoadMoreSearchResultAdapter;
import com.go4.application.listener.SearchResultEndlessRecyclerOnScrollListener;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DataController dataController;

    private LoadMoreSearchResultAdapter loadMoreSearchResultAdapter;
    private List<Map<String, String>> dataList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Map<String, String>> resultDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setTitle("Movie Search");
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchSyntax = editTextSearch.getText().toString();
                if (searchSyntax.contains("search:")) {
                    parseAndSearch(searchSyntax);
                } else {
                    normalSearch(searchSyntax);
                }
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Create a data controller to read data files
        dataController = new DataController(this);
        resultDataset = dataController.getMovieData();
        getData(resultDataset);

        // Create custom list adapter
        loadMoreSearchResultAdapter = new LoadMoreSearchResultAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(loadMoreSearchResultAdapter);

        // Set dropdown refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh data
                dataList.clear();
                getData(resultDataset);
                loadMoreSearchResultAdapter.notifyDataSetChanged();

                // Delay for 1 second to close dropdown refresh
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        // Set to load more listeners
        recyclerView.addOnScrollListener(new SearchResultEndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING);

                int size = resultDataset.size();
                if (dataList.size() < size) {
                    // Simulate obtaining network data with a delay of 1 second
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData(resultDataset);
                                    loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_COMPLETE);
                                }
                            });
                        }
                    }, 1000);
                } else {
                    // Display a prompt to load to the end
                    loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_END);
                }
            }
        });

        loadMoreSearchResultAdapter.setOnItemClickListener(new LoadMoreSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> data) {
                // Process the RecyclerView sub item click event and open the detailed movie page
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("movie_data", (Serializable)data);
                startActivity(intent);
            }
        });
    }

    // Get 20 latest data each time
    private void getData(List<Map<String, String>> data) {
        int size = data.size();
        for (int i = 0; i < 20; i++) {
            int n = dataList.size();
            if (n >= size) {
                break;
            }
            dataList.add(data.get(n));
        }
    }

    // Search for keywords in the normal way, only search for movie names that match the keywords
    private void normalSearch(String searchSyntax) {
        List<Map<String, String>> movieData = dataController.getMovieData();
        if (movieData == null || searchSyntax.length() == 0) {
            resultDataset = movieData;
            dataList.clear();
            getData(resultDataset);
            loadMoreSearchResultAdapter.notifyDataSetChanged();
            return;
        }

        resultDataset.clear();
        String[] items = searchSyntax.split(" ");
        for (int i = 0; i < movieData.size(); i++) {
            for (String item : items) {
                Map<String, String> map = movieData.get(i);
                if (map.get("title").contains(item)) {
                    resultDataset.add(map);
                }
            }
        }

        // Clear the display list and display the searched data again
        dataList.clear();
        getData(resultDataset);
        loadMoreSearchResultAdapter.notifyDataSetChanged();
        if (resultDataset.size() == 0) {
            Toast.makeText(this, "No matching movie found!", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement syntax search
    // e.g. search:name:and word1 word2 word3
    // e.g. search:name:or word1 word2 word3
    // e.g. search:title:or word1 word2 word3
    // e.g. search:averageRating:or word1 word2 word3
    public void parseAndSearch(String query) {
        // Define regular expression pattern
        String pattern = "search:(name|title|genres|year|averageRating):(AND|OR|and|or) ([\\w\\s]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(query);

        resultDataset.clear();
        if (m.find()) {
            String field = m.group(1);
            String operator = m.group(2);
            String[] keywords = m.group(3).split(" ");
            if (field.equals("name")) {
                field = "title";
            }

            // Grammar matching search
            List<Map<String, String>> movieData = dataController.getMovieData();
            for (Map<String, String> map: movieData) {
                boolean found = false;
                if ("OR".equals(operator) || "or".equals(operator)) {
                    for (String keyword : keywords) {
                        if (map.containsKey(field) && map.get(field).contains(keyword)) {
                            found = true;
                            break;
                        }
                    }
                } else if ("AND".equals(operator) || "and".equals(operator)) {
                    found = true;
                    for (String keyword : keywords) {
                        if (map.containsKey(field)) {
                            if (!map.get(field).contains(keyword)) {
                                found = false;
                                break;
                            }
                        } else {
                            found = false;
                            break;
                        }
                    }
                }

                if (found) {
                    resultDataset.add(map);
                }
            }
        }

        dataList.clear();
        getData(resultDataset);
        loadMoreSearchResultAdapter.notifyDataSetChanged();

        if (resultDataset.size() == 0) {
            Toast.makeText(this, "No matching movie found!", Toast.LENGTH_SHORT).show();
        }
    }
}
