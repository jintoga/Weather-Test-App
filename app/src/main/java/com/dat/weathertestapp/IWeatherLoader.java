package com.dat.weathertestapp;

import retrofit.client.Response;

/**
 * Created by DAT on 11/16/2016.
 */

public interface IWeatherLoader {
    void loading();

    void parseCurrentDayWeather(Response response);

    void parseWeathersForecast16(Response response);

    void loadSuccessful();

    void showError(Throwable e);
}
