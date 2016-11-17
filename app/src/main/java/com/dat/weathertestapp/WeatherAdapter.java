package com.dat.weathertestapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.dat.weathertestapp.model.Weather;
import com.dat.weathertestapp.utils.TextUtil;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DAT on 11/16/2016.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private ArrayList<Weather> weatherData;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (weatherData != null && weatherData.size() > 0) {
            Weather weather = weatherData.get(position);
            if (weather != null) {
                holder.time.setText(getTimeAndDate(weather.getDt()));
                Picasso.with(holder.weatherIcon.getContext())
                    .load(weather.getIconURL())
                    .into(holder.weatherIcon);
                String weatherResult = "";

                String temperature = "Температура: ";
                temperature += "День: " + TextUtil.sizeSpan(
                    TextUtil.celsius(weather.getTemperature().getDay()), 18);
                temperature += ", Ночь: " + TextUtil.sizeSpan(
                    TextUtil.celsius(weather.getTemperature().getNight()), 18);

                String wind = "Ветер: ";
                wind += new TextUtil.DegreeToDirection(weather.getWindDeg()).convert();
                wind += ", " + TextUtil.windUnit(weather.getWindSpeed());

                String pressure = "Давление: ";
                pressure += TextUtil.pressureUnit(
                    new TextUtil.HPAtoTorrConverter(weather.getPressure()).convert());

                String humidity = "Влажность: ";
                humidity += TextUtil.humidityUnit(weather.getHumidity());
                weatherResult = temperature + "\n" + wind + "\n" + pressure + "\n" + humidity;
                holder.weather.setText(weatherResult);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (weatherData != null) {
            return weatherData.size();
        }
        return 0;
    }

    public void setWeatherData(List<Weather> data) {
        if (weatherData == null) {
            weatherData = new ArrayList<>();
        }
        weatherData.clear();
        weatherData.addAll(data);
        notifyDataSetChanged();
    }

    private String getTimeAndDate(long epoch) {
        Date date = new Date(epoch * 1000L);
        String res = "";
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("cccc", Locale.getDefault());
        res += dayOfWeek.format(date) + "\n";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        res += simpleDateFormat.format(date);

        return res;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time)
        protected TextView time;
        @Bind(R.id.weather)
        protected TextView weather;
        @Bind(R.id.weatherIcon)
        protected ImageView weatherIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
