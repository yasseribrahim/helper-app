package com.helper.app.utils;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.helper.app.models.LocationModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocationUtils {
    private static final LocationUtils UTILS = new LocationUtils();
    private LocationModel previousModel;

    private LocationUtils() {
    }

    public static LocationUtils getInstance() {
        return UTILS;
    }

    public LocationModel calculateSpeed(Location location) {
        LocationModel model = new LocationModel(location.getLatitude(), location.getLongitude(), Calendar.getInstance().getTime());

        float initialSpeed = 0;
        if (previousModel != null) {
            initialSpeed = previousModel.getSpeed();
        }

        float speed = 0;
        if (location.getSpeed() != 0) {
            speed = location.getSpeed();
        } else if (previousModel != null) {
            double distance = distance(location.getLatitude(), location.getLongitude(), previousModel.getLatitude(), previousModel.getLongitude(), 'K');
            float time = (float) ((model.getDate().getTime() - previousModel.getDate().getTime()) / (1000.0 * 60 * 60));

            if (time > 0) {
                speed = (float) (distance / time);
            }
        }
        speed = speed + initialSpeed;
        model.setSpeed(speed);

        previousModel = model;
        return model;
    }

    public double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public List<LocationModel> getTrackTest() {
        List<LocationModel> models = new Gson().fromJson(data, new TypeToken<List<LocationModel>>() {
        }.getType());
        return models;
    }

    public List<Float> getSpeedTest() {
        List<Float> speeds = new ArrayList<>();
        for (int i = 0; i <= 200; i++) {
            speeds.add((float) i);
        }
        return speeds;
    }

    private String data = "[{\n" +
            "\t\"latitude\": 29.877573423487846,\n" +
            "\t\"longitude\": 31.28915625744114,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:00 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.878829305133966,\n" +
            "\t\"longitude\": 31.28932791880688,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:03 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.880736354427842,\n" +
            "\t\"longitude\": 31.289370834148308,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:06 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.882494528567236,\n" +
            "\t\"longitude\": 31.289349376470582,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:09 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.884457321270947,\n" +
            "\t\"longitude\": 31.28905969789627,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:12 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.88668983473458,\n" +
            "\t\"longitude\": 31.289252816932727,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:15 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.889043221885245,\n" +
            "\t\"longitude\": 31.289199172758735,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:18 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.891182616533953,\n" +
            "\t\"longitude\": 31.288952409535675,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:21 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.893777733632415,\n" +
            "\t\"longitude\": 31.28829795052483,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:24 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.896614612264404,\n" +
            "\t\"longitude\": 31.28768640685121,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:27 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.898735242583,\n" +
            "\t\"longitude\": 31.28753620312956,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:30 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.901162750830935,\n" +
            "\t\"longitude\": 31.28795462774009,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:33 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.904138925429177,\n" +
            "\t\"longitude\": 31.28849106951713,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:36 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.90637099805004,\n" +
            "\t\"longitude\": 31.28820139095193,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:39 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.90882854517414,\n" +
            "\t\"longitude\": 31.2869085661987,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:42 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.91070247127419,\n" +
            "\t\"longitude\": 31.286812006673113,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:45 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.91210208042548,\n" +
            "\t\"longitude\": 31.286833464342077,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:48 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.91385040176434,\n" +
            "\t\"longitude\": 31.28668862506263,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:51 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.915510348682357,\n" +
            "\t\"longitude\": 31.286554514616796,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:54 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.917077275988298,\n" +
            "\t\"longitude\": 31.286565243448656,\n" +
            "\t\"date\": \"Nov 5, 2022 2:53:57 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.91910913015075,\n" +
            "\t\"longitude\": 31.28623801395445,\n" +
            "\t\"date\": \"Nov 5, 2022 2:54:00 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.92102470754828,\n" +
            "\t\"longitude\": 31.2857766740186,\n" +
            "\t\"date\": \"Nov 5, 2022 2:55:03 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}, {\n" +
            "\t\"latitude\": 29.923433075385237,\n" +
            "\t\"longitude\": 31.284398018528524,\n" +
            "\t\"date\": \"Nov 5, 2022 2:56:06 PM\",\n" +
            "\t\"speed\": 0.0\n" +
            "}]";
}
