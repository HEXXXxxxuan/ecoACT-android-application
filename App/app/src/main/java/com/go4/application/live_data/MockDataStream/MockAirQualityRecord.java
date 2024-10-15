package com.go4.application.live_data.MockDataStream;

import java.util.ArrayList;
import java.util.List;
/**
 * This class represents a mock air quality record containing coordinates and a list of air quality data.
 * used to simulate air quality data in a structured format for testing purposes.
 * The class includes nested static classes to mimic the real-world json response
 * provided by OpenWeather API.
 *
 * @author u7902000 Gea Linggar
 */
public class MockAirQualityRecord {
    private Coord coord;
    private List<AirQualityData> list;

    public MockAirQualityRecord(double lon, double lat, AirQualityData data) {
        this.coord = new Coord(lon, lat);
        this.list = new ArrayList<>();
        this.list.add(data);
    }

    public static class Coord {
        double lon;
        double lat;

        public Coord(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
        }
    }

    public static class AirQualityData {
        Main main;
        Components components;
        long dt;

        public AirQualityData(double aqi, Components components, long dt) {
            this.main = new Main(aqi);
            this.components = components;
            this.dt = dt;
        }

        public static class Main {
            double aqi;

            public Main(double aqi) {
                this.aqi = aqi;
            }
        }

        public static class Components {
            double co;
            double no2;
            double o3;
            double so2;
            double pm2_5;
            double pm10;

            public Components(double co, double no2, double o3, double so2, double pm2_5, double pm10) {
                this.co = co;
                this.no2 = no2;
                this.o3 = o3;
                this.so2 = so2;
                this.pm2_5 = pm2_5;
                this.pm10 = pm10;
            }
        }
    }
}
