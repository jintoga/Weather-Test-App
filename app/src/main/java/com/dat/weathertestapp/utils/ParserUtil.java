package com.dat.weathertestapp.utils;

import android.util.Log;
import com.dat.weathertestapp.model.Weather;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DAT on 11/16/2016.
 */

public class ParserUtil {
    public static class CurrentDayWeather {
        private String json;

        public CurrentDayWeather(String json) {
            this.json = json;
        }

        public Weather parse() {
            Weather weather;
            try {
                JSONObject jsonObject = new JSONObject(json);
                weather = new Weather();
                weather.setDt(jsonObject.getLong("dt"));
                weather.setCityName(jsonObject.getString("name"));
                JSONArray detail = jsonObject.getJSONArray("weather");
                weather.setDescription(detail.getJSONObject(0).getString("description"));
                weather.setIcon(detail.getJSONObject(0).getString("icon"));
                JSONObject main = jsonObject.getJSONObject("main");
                weather.setCur_temp(main.getDouble("temp"));
                weather.setPressure(main.getDouble("pressure"));
                weather.setHumidity(main.getDouble("humidity"));
                JSONObject wind = jsonObject.getJSONObject("wind");
                weather.setWindSpeed(wind.getDouble("speed"));
                weather.setWindDeg(wind.getDouble("deg"));
                return weather;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("WeatherEr", e.toString());
                return null;
            }
        }
    }

    public static class Next16DaysWeather {
        private String json;

        public Next16DaysWeather(String json) {
            this.json = json;
        }

        public ArrayList<Weather> parse() {
            ArrayList<Weather> weathers;
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                if (jsonArray != null && jsonArray.length() > 0) {
                    weathers = new ArrayList<>();
                } else {
                    return null;
                }
                for (int i = 1; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Weather weather = new Weather();
                    weather.setDt(object.getLong("dt"));
                    weather.setPressure(object.getDouble("pressure"));
                    weather.setHumidity(object.getDouble("humidity"));
                    JSONObject jsonObjectTemp = object.getJSONObject("temp");
                    Weather.Temperature temp = new Weather.Temperature();
                    temp.setDay(jsonObjectTemp.getDouble("day"));
                    temp.setNight(jsonObjectTemp.getDouble("night"));
                    temp.setEve(jsonObjectTemp.getDouble("eve"));
                    temp.setMorn(jsonObjectTemp.getDouble("morn"));
                    weather.setTemperature(temp);
                    JSONArray detail = object.getJSONArray("weather");
                    weather.setDescription(detail.getJSONObject(0).getString("description"));
                    weather.setIcon(detail.getJSONObject(0).getString("icon"));
                    weathers.add(weather);
                }
                return weathers;
            } catch (JSONException e) {
                Log.e("WeatherEr", e.toString());
                e.printStackTrace();
                return null;
            }
        }
    }
}
