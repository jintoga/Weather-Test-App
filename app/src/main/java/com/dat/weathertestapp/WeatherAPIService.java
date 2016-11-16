package com.dat.weathertestapp;

import java.util.Map;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by DAT on 11/16/2016.
 */

public class WeatherAPIService {
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5";
    private WeatherAPI weatherAPI;

    public WeatherAPIService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(OPEN_WEATHER_MAP_URL)
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

        weatherAPI = restAdapter.create(WeatherAPI.class);
    }

    public WeatherAPI getAPI() {
        return weatherAPI;
    }

    public interface WeatherAPI {
        @GET("/weather")
        Observable<Response> getCurrentDayWeather(@QueryMap Map<String, String> params);

        @GET("/forecast/daily")
        Observable<Response> getWeathersForecast16(@QueryMap Map<String, String> params);
    }
}
