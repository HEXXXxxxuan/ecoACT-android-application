package com.go4.application.historical.MovieDemo.Model;

public class Movie {

    private int movieId;
    private String title;
    private String genres;
    private int year;
    private double averageRating;
    private double maleAverageRating;
    private double femaleAverageRating;
    private int maleCount;
    private int femaleCount;

    public Movie(int movieId, String title, String genres, int year, double averageRating, double maleAverageRating, double femaleAverageRating, int maleCount, int femaleCount) {
        this.movieId = movieId;
        this.title = title;
        this.genres = genres;
        this.year = year;
        this.averageRating = averageRating;
        this.maleAverageRating = maleAverageRating;
        this.femaleAverageRating = femaleAverageRating;
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // getters and setters for maleAverageRating
    public double getMaleAverageRating() {
        return maleAverageRating;
    }

    public void setMaleAverageRating(double maleAverageRating) {
        this.maleAverageRating = maleAverageRating;
    }

    public double getFemaleAverageRating() {
        return femaleAverageRating;
    }

    public void setFemaleAverageRating(double femaleAverageRating) {
        this.femaleAverageRating = femaleAverageRating;
    }

    public int getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(int maleCount) {
        this.maleCount = maleCount;
    }

    public int getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(int femaleCount) {
        this.femaleCount = femaleCount;
    }
}
