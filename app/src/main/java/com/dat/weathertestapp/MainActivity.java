package com.dat.weathertestapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.dat.weathertestapp.model.Weather;
import com.dat.weathertestapp.utils.ParserUtil;
import com.dat.weathertestapp.utils.TextUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MainActivity extends AppCompatActivity implements IWeatherLoader {

    private static final String KEY_CURRENT_WEATHER = "CURRENT_WEATHER";
    private static final String KEY_FORECAST16_WEATHER = "FORECAST_WEATHER";

    private static final String city_id_key = "id";
    private static final String unit_key = "units";
    private static final String lang_key = "lang";
    private static final String app_id_key = "appid";
    private static final String count_key = "cnt";

    private static final String TAG = MainActivity.class.getName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @Bind(R.id.todayWeather)
    protected TextView todayWeather;
    @Bind(R.id.imageViewCurDay)
    protected ImageView imageViewCurDay;
    @Bind(R.id.loading)
    protected ProgressBar loading;

    private WeatherPresenter presenter;

    private WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new WeatherPresenter(new WeatherAPIService(), this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initRecyclerView();
        restoreWeatherData();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoading();
    }

    private void startLoading() {

        Map<String, String> params = new HashMap<>();
        String barnaulWeatherID = getString(R.string.barnaul_weather_id);
        params.put(city_id_key, barnaulWeatherID);
        params.put(unit_key, getResources().getString(R.string.weather_units));
        params.put(lang_key, getResources().getString(R.string.weather_lang));
        params.put(app_id_key, getResources().getString(R.string.open_weather_map_AppID));

        Map<String, String> params16 = new HashMap<>();
        params16.put(city_id_key, barnaulWeatherID);
        params16.put(unit_key, getResources().getString(R.string.weather_units));
        params16.put(lang_key, getResources().getString(R.string.weather_lang));
        params16.put(app_id_key, getResources().getString(R.string.open_weather_map_AppID));
        params16.put(count_key, getResources().getString(R.string.count_key));

        presenter.loadWeather(params, params16);
    }

    @Override
    public void loading() {
        Log.d(TAG, "Loading");
        loading.setVisibility(View.VISIBLE);
    }

    private void bindCurrentDayWeatherData(Weather weather) {
        if (weather != null) {
            Picasso.with(this).load(weather.getIconURL()).into(imageViewCurDay);
            String weatherResult;
            String temperature = "Температура: ";
            temperature += TextUtil.sizeSpan(TextUtil.celsius(weather.getCur_temp()), 26);

            String wind = "Ветер: ";
            wind += new TextUtil.DegreeToDirection(weather.getWindDeg()).convert();
            wind += ", " + TextUtil.windUnit(weather.getWindSpeed());

            String pressure = "Давление: ";
            pressure += TextUtil.pressureUnit(
                new TextUtil.HPAtoTorrConverter(weather.getPressure()).convert());

            String humidity = "Влажность: ";
            humidity += TextUtil.humidityUnit(weather.getHumidity());
            weatherResult = temperature + "\n" + wind + "\n" + pressure + "\n" + humidity;
            todayWeather.setText(weatherResult);
            imageViewCurDay.setVisibility(View.VISIBLE);
        } else {
            todayWeather.setVisibility(View.GONE);
            imageViewCurDay.setVisibility(View.GONE);
        }
    }

    @Override
    public void parseCurrentDayWeather(Response response) {
        Log.d(TAG, response.toString());
        String res = new String(((TypedByteArray) response.getBody()).getBytes());
        Weather curWeather = new ParserUtil.CurrentDayWeather(res).parse();
        bindCurrentDayWeatherData(curWeather);
        saveCurWeather(curWeather);
    }

    @Override
    public void parseWeathersForecast16(Response response) {
        Log.d(TAG, response.toString());
        String res = new String(((TypedByteArray) response.getBody()).getBytes());
        List<Weather> weathersForecast16 = new ParserUtil.Next16DaysWeather(res).parse();
        if (weathersForecast16 != null) {
            adapter.setWeatherData(weathersForecast16);
            saveForecastWeather(weathersForecast16);
        }
    }

    private void saveCurWeather(@Nullable final Weather curWeather) {
        if (curWeather != null) {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String curWeatherStr = gson.toJson(curWeather);
            editor.putString(KEY_CURRENT_WEATHER, curWeatherStr);
            editor.apply();
        }
    }

    private void saveForecastWeather(@NonNull List<Weather> weathersForecast16) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String forecastWeatherStr = gson.toJson(weathersForecast16);
        editor.putString(KEY_FORECAST16_WEATHER, forecastWeatherStr);
        editor.apply();
    }

    private void restoreWeatherData() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        //saved current day's weather
        String curWeatherStr = sharedPreferences.getString(KEY_CURRENT_WEATHER, null);
        if (curWeatherStr != null) {
            Gson gson = new Gson();
            Weather weather = gson.fromJson(curWeatherStr, Weather.class);
            bindCurrentDayWeatherData(weather);
        }
        //saved forecast 16 days weather
        String forecastWeatherStr = sharedPreferences.getString(KEY_FORECAST16_WEATHER, null);
        if (forecastWeatherStr != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Weather>>() {
            }.getType();
            List<Weather> weathersForecast16 = gson.fromJson(forecastWeatherStr, type);
            if (weathersForecast16 != null) {
                adapter.setWeatherData(weathersForecast16);
            }
        }
    }

    @Override
    public void loadSuccessful() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        loading.setVisibility(View.GONE);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            startLoading();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
