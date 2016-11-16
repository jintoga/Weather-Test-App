package com.dat.weathertestapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DAT on 11/16/2016.
 */

public class Weather {
    private long dt;
    private Temperature temperature;
    private double windSpeed;
    private double windDeg;
    private double pressure;
    private double humidity;
    private String description;
    private String icon;
    private String iconURL;
    private double cur_temp;
    private String dtString;

    private String cityName;

    private final static String URL_PREFIX = "http://openweathermap.org/img/w/";

    private final static String URL_SUFFIX = ".png";

    public Weather() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDeg() {
        return windDeg;
    }

    public void setWindDeg(double windDeg) {
        this.windDeg = windDeg;
    }

    public double getCur_temp() {
        return cur_temp;
    }

    public void setCur_temp(double cur_temp) {
        this.cur_temp = cur_temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
        setIconURL(URL_PREFIX + icon + URL_SUFFIX);
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
        convertEpoch(dt);
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getDtString() {
        return dtString;
    }

    public void setDtString(String dtString) {
        this.dtString = dtString;
    }

    private void convertEpoch(long dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        setDtString(sdf.format(new Date(dt * 1000L)));
    }

    public static class Temperature {
        double morn;
        double day;
        double eve;
        double night;

        public Temperature() {
        }

        public double getMorn() {
            return morn;
        }

        public void setMorn(double morn) {
            this.morn = morn;
        }

        public double getDay() {
            return day;
        }

        public void setDay(double day) {
            this.day = day;
        }

        public double getEve() {
            return eve;
        }

        public void setEve(double eve) {
            this.eve = eve;
        }

        public double getNight() {
            return night;
        }

        public void setNight(double night) {
            this.night = night;
        }
    }
}
