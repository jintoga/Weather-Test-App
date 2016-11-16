package com.dat.weathertestapp;

import android.os.Bundle;
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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MainActivity extends AppCompatActivity implements IWeatherLoader {

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

    private static final String TAG = MainActivity.class.getName();
    private WeatherPresenter presenter;

    private Weather curWeather;
    private ArrayList<Weather> weathersForecast16;
    private WeatherAdapter adapter;

    private static final String city_id_key = "id";
    private static final String unit_key = "units";
    private static final String lang_key = "lang";
    private static final String app_id_key = "appid";
    private static final String count_key = "cnt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new WeatherPresenter(new WeatherAPIService(), this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initRecyclerView();
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

    private void setCurrentDayWeatherData(Weather weather) {
        if (weather != null) {
            Picasso.with(this).load(weather.getIconURL()).into(imageViewCurDay);
            String weatherResult;
            String temperature = "Temperature: ";
            temperature += TextUtil.sizeSpan(TextUtil.celsius(weather.getCur_temp()), 26);

            String wind = "Wind: ";
            wind += new TextUtil.DegreeToDirection(weather.getWindDeg()).convert();
            wind += ", " + TextUtil.windUnit(weather.getWindSpeed());

            String pressure = "Pressure: ";
            pressure += TextUtil.pressureUnit(
                new TextUtil.HPAtoTorrConverter(weather.getPressure()).convert());

            String humidity = "Humidity: ";
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
        Log.d("Weather:", response.toString());
        String res = new String(((TypedByteArray) response.getBody()).getBytes());
        curWeather = new ParserUtil.CurrentDayWeather(res).parse();
        setCurrentDayWeatherData(curWeather);
    }

    @Override
    public void parseWeathersForecast16(Response response) {
        Log.d("Weather:", response.toString());
        String res = new String(((TypedByteArray) response.getBody()).getBytes());
        weathersForecast16 = new ParserUtil.Next16DaysWeather(res).parse();
        if (weathersForecast16 != null) {
            adapter.setWeatherData(weathersForecast16);
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
