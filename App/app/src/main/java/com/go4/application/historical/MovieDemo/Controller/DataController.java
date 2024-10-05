package com.go4.application.historical.MovieDemo.Controller;

import android.content.Context;

import com.go4.application.historical.MovieDemo.Model.Movie;
import com.go4.application.historical.MovieDemo.Model.Rating;
import com.go4.application.historical.MovieDemo.Model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataController {
    private Context context;
    private List<Movie> movies;
    private List<User> users;
    private List<Rating> ratings;

    public DataController(Context context) {
        this.context = context;
        readData(context);
    }

    public void readData(Context context) {
        // read movies.dat
        movies = readMovieCsvData(context, "movies.csv");
        //movies = readMoviesData(context, "movies.dat");

        // read users.dat
        //users = readUserData(context, "users.dat");

        // read ratings.dat
        //ratings = readRatingData(context, "ratings.dat");
    }

    // Read movie CSV data files
    public static List<Movie> readMovieCsvData(Context context, String filePath) {
        List<Movie> movies = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = parseCSVLine(line);

                String title = data[1];
                int start = title.lastIndexOf("(");
                int year = Integer.parseInt(title.substring(start + 1, title.lastIndexOf(")")));
                title = title.substring(0, start).trim();

                if (data[3].isEmpty() || data[4].isEmpty() || data[5].isEmpty() || data[6].isEmpty() || data[7].isEmpty()) {
                    continue;
                }

                double averageRating = Double.parseDouble(data[3]);
                double maleAverageRating = Double.parseDouble(data[4]);
                double femaleAverageRating = Double.parseDouble(data[5]);
                int maleCount = (int)Double.parseDouble(data[6]);
                int femaleCount = (int)Double.parseDouble(data[7]);
                Movie movie = new Movie(Integer.parseInt(data[0]), title, data[2], year, averageRating, maleAverageRating, femaleAverageRating, maleCount, femaleCount);
                movies.add(movie);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Analyze CSV format data
    // Convert a comma separated string into a string array and return it
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder value = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }

        values.add(value.toString());

        return values.toArray(new String[0]);
    }

    // Not currently used
    public static List<Movie> readMoviesData(Context context, String filePath) {
        List<Movie> movies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filePath)))) {
            String line;
            while ((line = reader.readLine())!= null) {
                String[] parts = line.split("::");
                if (parts.length == 3) {
                    int movieId = Integer.parseInt(parts[0]);
                    String title = parts[1];
                    String genres = parts[2];

                    int start = title.lastIndexOf("(");
                    int year = Integer.parseInt(title.substring(start + 1, title.lastIndexOf(")")));
                    title = title.substring(0, start).trim();

                    //Movie movie = new Movie(movieId, title, genres, year);
                    //movies.add(movie);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Not currently used
    public static List<User> readUserData(Context context, String filePath) {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filePath)))) {
            String line;
            while ((line = reader.readLine())!= null) {
                String[] parts = line.split("::");
                if (parts.length == 5) {
                    int userId = Integer.parseInt(parts[0]);
                    String gender = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    String occupation = parts[3];
                    String zipCode = parts[4];
                    User user = new User(userId, gender, age, occupation, zipCode);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Quickly split strings
    public static String[] customSplit(String input) {
        String[] result = new String[3];
        int s = input.indexOf("::");
        result[0] = input.substring(0, s);

        s += 2;
        int s2 = input.indexOf("::", s);
        result[1] = input.substring(s, s2);

        s2 += 2;
        s = input.indexOf("::", s2);
        result[2] = input.substring(s2, s);

        //result[3] = input.substring(s + 2);
        return result;
    }

    // Not currently used
    public static List<Rating> readRatingData(Context context, String filePath) {
        List<Rating> ratings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filePath)))) {
            String line;
            while ((line = reader.readLine())!= null) {
                //String[] parts = line.split("::");
                String[] parts = customSplit(line);
                if (parts.length == 3) {
                    int userId = Integer.parseInt(parts[0]);
                    int movieId = Integer.parseInt(parts[1]);
                    int rating = Integer.parseInt(parts[2]);
                    //long timestamp = Long.parseLong(parts[3]);
                    Rating ratingObj = new Rating(userId, movieId, rating, 0);
                    ratings.add(ratingObj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    // Convert object data to a map
    public List<Map<String, String>> getMovieData() {
        List<Map<String, String>> result = new ArrayList<>();
        for (Movie movie : movies) {
            Map<String, String> item = new HashMap<>();
            item.put("id", movie.getMovieId() + "");
            item.put("title", movie.getTitle());
            item.put("genres", movie.getGenres());
            item.put("year", movie.getYear() + "");
            item.put("averageRating", movie.getAverageRating() + "");
            item.put("maleAverageRating", movie.getMaleAverageRating() + "");
            item.put("maleCount", movie.getMaleCount() + "");
            item.put("femaleAverageRating", movie.getFemaleAverageRating() + "");
            item.put("femaleCount", movie.getFemaleCount() + "");
            result.add(item);
        }
        return result;
    }
}
