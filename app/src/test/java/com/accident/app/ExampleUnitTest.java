package com.accident.app;

import com.google.gson.Gson;
import com.accident.warning.system.app.models.LocationModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

    }


    @Test
    public void addition_isCorrect() {
        String[] lines = data.split("\n");
        List<LocationModel> models = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int counter = 0;
        for (String line : lines) {
            String[] values = line.split(", ");

            double latitude = Double.parseDouble(values[0]);
            double longitude = Double.parseDouble(values[1]);
            calendar.set(Calendar.SECOND, counter);
            counter += 3;
            Date date = calendar.getTime();
            LocationModel model = new LocationModel(latitude, longitude, date);
            models.add(model);
        }

        System.out.println(new Gson().toJson(models));
    }

    String data = "29.877573423487846, 31.28915625744114\n" +
            "29.878829305133966, 31.28932791880688\n" +
            "29.880736354427842, 31.289370834148308\n" +
            "29.882494528567236, 31.289349376470582\n" +
            "29.884457321270947, 31.28905969789627\n" +
            "29.88668983473458, 31.289252816932727\n" +
            "29.889043221885245, 31.289199172758735\n" +
            "29.891182616533953, 31.288952409535675\n" +
            "29.893777733632415, 31.28829795052483\n" +
            "29.896614612264404, 31.28768640685121\n" +
            "29.898735242583, 31.28753620312956\n" +
            "29.901162750830935, 31.28795462774009\n" +
            "29.904138925429177, 31.28849106951713\n" +
            "29.90637099805004, 31.28820139095193\n" +
            "29.90882854517414, 31.2869085661987\n" +
            "29.91070247127419, 31.286812006673113\n" +
            "29.91210208042548, 31.286833464342077\n" +
            "29.91385040176434, 31.28668862506263\n" +
            "29.915510348682357, 31.286554514616796\n" +
            "29.917077275988298, 31.286565243448656\n" +
            "29.91910913015075, 31.28623801395445\n" +
            "29.92102470754828, 31.2857766740186\n" +
            "29.923433075385237, 31.284398018528524";
}