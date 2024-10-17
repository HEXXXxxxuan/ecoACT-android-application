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
import android.widget.Button;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4.application.FirebaseLoginActivity;
import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.SuburbJsonUtils;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;


/**
 * This activity launches the profile page after successful login.
 *
 * <p>It displays user email, allows users to change profile picture, and add, rename and delete suburb cards</p>
 * @author u8003980 Chan Cheng Leong
 */
public class ProfileActivity extends AppCompatActivity {
    private Spinner suburbSpinner;
    private ImageView imageView;
    private AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;
    private Handler handler;
    private Runnable updateRunnable;
    private String email;

    private RecyclerView suburbCardList;
    private ArrayList<SuburbCard> recyclerDataArrayList;
    private SuburbCardViewAdapter recyclerViewAdapter;

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
        email = intent.getStringExtra("displayName");
        TextView username = findViewById(R.id.pa_username);
        username.setText(email);

        // Create AVL Tree
        createAVLTree();

        // Set up suburb spinner
        suburbSpinner = findViewById(R.id.pa_suburb_spinner);
        suburbSpinner();

        // Logout button
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent logoutIntent = new Intent(ProfileActivity.this, FirebaseLoginActivity.class);
            startActivity(logoutIntent);
            finish();
        });

        // Load and Save Profile Picture
        imageView = findViewById(R.id.pa_profile);
        editableProfilePicture();

        // Display Pinned Suburb Cards
        suburbCardList = findViewById(R.id.pa_cardList);
        displayPinnedSuburbCards();

        // Set up ADD pinned suburb button
        Button addButton = findViewById(R.id.pa_add_button);
        addButton.setOnClickListener(v -> addButtonOnClick());

        // Nav Bar
        LinearLayout profile = findViewById(R.id.nav_profile);
        LinearLayout suburb_live = findViewById(R.id.nav_suburb_live);

        suburb_live.setOnClickListener(
            v -> {
                Intent suburbLiveIntent = new Intent(ProfileActivity.this, SuburbLiveActivity.class);
                suburbLiveIntent.putExtra("displayName", email);
                startActivity(suburbLiveIntent);
            }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler();

        // Update cards every 15 minutes
        updateRunnable = new Runnable() {
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
     *
     * <p>With reference to <a href="https://developer.android.com/training/data-storage/shared/photopicker">this website</a></p>
     */
    private void editableProfilePicture() {
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

    /**
     * This method returns the file path where the profile picture is stored in internal storage
     */
    private String getFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String filename = "profile_picture.jpg";
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        return String.format("%s/%s", directory, filename);
    }

    /**
     * This method reads and shows the profile picture on screen.
     *
     * <p>The image of the profile picture is obtained from the function getFilePath()</p>
     */
    private void readProfilePicture() {
        String filePath = getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        } else {
            imageView.setImageResource(R.drawable.default_profile);
        }
    }

    /**
     * This method writes and stores the profile picture in internal storage.
     *
     * <p>The image of the profile picture is stored in the path returned from the function getFilePath()</p>
     */
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

    /**
     * This method displays Suburb Cards using {@link SuburbCardViewAdapter} and {@link SuburbCard}
     *
     * <p>Swipe right on suburb card to delete it, and press undo button to undo.
     * With reference to <a href="https://www.geeksforgeeks.org/how-to-add-dividers-in-android-recyclerview/">this website</a></p>
     */
    private void displayPinnedSuburbCards() {
        recyclerDataArrayList = new ArrayList<>();
        readPinnedSuburbs();
        recyclerViewAdapter = new SuburbCardViewAdapter(recyclerDataArrayList);

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
                SuburbCard deletedSuburbCard = recyclerDataArrayList.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                writePinnedSuburbs();

                Snackbar.make(suburbCardList, deletedSuburbCard.getLabel(), Snackbar.LENGTH_LONG).setAction("Undo", v -> {
                    recyclerDataArrayList.add(position, deletedSuburbCard);
                    recyclerViewAdapter.notifyItemInserted(position);
                    writePinnedSuburbs();
                }).show();
            }
        }).attachToRecyclerView(suburbCardList);
    }

    /**
     * The method is called to search for the current PM10 number and air quality index based on the suburb.
     *
     * @param suburb           The name of the suburb to search for
     * @return                 A list of 2 strings, the first string contains whether the quality is "Good", "Moderate" or "Bad", and the second string contains the PM10 number.
     */
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

    private void readPinnedSuburbs() {
        recyclerDataArrayList = new ArrayList<SuburbCard>();
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

    /**
     * This method writes the current pinned suburb details to internal storage,
     * to the file "pinned_suburbs.txt"
     *
     * <p>This method is called when a pinned suburb is added, deleted, or when the labels of the pinned suburbs are edited, or when the activity is paused or stopped.</p>
     */
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
            for (SuburbCard card: recyclerDataArrayList) {
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

    /**
     * This method updates the quality and the PM10 number of every pinned suburb.
     *
     * <p>This method is called every 15 minutes to update the pinned suburb cards.</p>
     */
    private void updatePinnedSuburbs() {
        for (SuburbCard card: recyclerDataArrayList) {
            String suburb = card.getSuburb();
            String[] result = searchForQualityAndPm10Number(suburb);
            String quality = result[0];
            String pm10Number = result[1];

            card.setQuality(quality);
            card.setPm10Number(pm10Number);
        }
        writePinnedSuburbs();
    }

    /**
     * This method searches for the quality and PM10 number of the selected suburb,
     * then uses the data to add a new suburb card.
     *
     * <p>This method is called when the add button is clicked.</p>
     */
    private void addButtonOnClick() {
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        String[] result = searchForQualityAndPm10Number(selectedSuburb);
        String quality = result[0];
        String pm10Number = result[1];

        String label = "Label (e.g. Home/Work/School)";
        addSuburbCard(label, selectedSuburb, quality, pm10Number);
        writePinnedSuburbs();
    }

    /**
     * This method adds a new pinned suburb card to the bottom of the list.
     *
     * <p>This method is called by addButtonOnClick().</p>
     *
     * @param label            The label of the suburb card.
     * @param suburb           The suburb of the suburb card.
     * @param quality          The quality of the suburb card. The quality can be "Good", "Moderate" or "Bad".
     * @param pm10Number       The PM10 number of the suburb card.
     */
    private void addSuburbCard(String label, String suburb, String quality, String pm10Number) {
        SuburbCard card = new SuburbCard(label, suburb, quality, pm10Number);
        recyclerDataArrayList.add(card);
        suburbCardList.setAdapter(recyclerViewAdapter);
    }

    /**
     * Initializes the {@code recordTreeLocationAndDateKey} by parsing a CSV file
     * and creating an AVL tree from the parsed data.
     *
     * @author u7902000 Gea Linggar
     */
    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    /**
     * Initializes the suburb spinner by loading the list of suburbs from a {@code loadSuburbsFromJson()} and populating
     * the spinner with this list.
     *
     * @author u7902000 Gea Linggar
     */
    private void suburbSpinner() {
        List<String> suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    /**
     * Loads a list of suburbs from the "canberra_suburbs.json" file in the assets folder.
     * <p>
     * This method delegates the actual loading process to the {@link SuburbJsonUtils}
     * which handles the JSON file parsing and returns a list of suburb names.
     * </p>
     *
     * @return a list of suburb names parsed from the "canberra_suburbs.json" file.
     * @throws RuntimeException if there is an error loading or parsing the JSON file.
     * @author u7902000 Gea Linggar
     */
    private List<String> loadSuburbsFromJson(){
        return SuburbJsonUtils.loadSuburbsFromJson(this, "canberra_suburbs.json");
    }
}