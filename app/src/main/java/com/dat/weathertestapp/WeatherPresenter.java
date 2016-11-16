package com.dat.weathertestapp;

import java.util.Map;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DAT on 11/16/2016.
 */

public class WeatherPresenter {
    private WeatherAPIService apiService;
    private IWeatherLoader weatherLoader;

    public WeatherPresenter(WeatherAPIService apiService, IWeatherLoader weatherLoader) {
        this.apiService = apiService;
        this.weatherLoader = weatherLoader;
    }

    public void loadWeather(final Map<String, String> paramsCur,
        final Map<String, String> params16) {
        weatherLoader.loading();
        Observable.concat(apiService.getAPI().getCurrentDayWeather(paramsCur),
            apiService.getAPI().getWeathersForecast16(params16))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Response>() {
                int count = 0;

                @Override
                public void onCompleted() {
                    weatherLoader.loadSuccessful();
                }

                @Override
                public void onError(Throwable e) {
                    weatherLoader.showError(e);
                }

                @Override
                public void onNext(Response response) {
                    count++;
                    if (count == 1) {
                        weatherLoader.parseCurrentDayWeather(response);
                    } else if (count == 2) {
                        weatherLoader.parseWeathersForecast16(response);
                    }
                }
            });
    }
}
