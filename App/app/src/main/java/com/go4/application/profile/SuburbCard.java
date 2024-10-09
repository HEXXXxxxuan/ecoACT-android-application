package com.go4.application.profile;

public class SuburbCard {
    private String label;
    private String suburb;
    private String quality;
    private String pm10Number;

    public SuburbCard(String label, String suburb, String quality, String pm10Number) {
        this.label = label;
        this.suburb = suburb;
        this.quality = quality;
        this.pm10Number = pm10Number;
    }

    public void setLabel(String label) { this.label = label; }

    public void setQuality(String quality) { this.quality = quality; }

    public void setPm10Number(String pm10Number) { this.pm10Number = pm10Number; }

    public String getLabel() { return label; }

    public String getSuburb() { return suburb; }

    public String getQuality() { return quality; }

    public String getPm10Number() { return pm10Number; }

    public String getData() {
        return label + "," + suburb + "," + quality + "," + pm10Number + "\n";
    }
}
