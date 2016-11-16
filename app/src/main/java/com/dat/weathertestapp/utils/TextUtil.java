package com.dat.weathertestapp.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;

/**
 * Created by DAT on 11/16/2016.
 */

public class TextUtil {
    public static Spannable celsius(double temp) {
        int roundedTemp = (int) Math.round(temp);
        String res = roundedTemp + "°C";
        return new SpannableString(res);
    }

    public static Spannable windUnit(double wind) {
        int rounded = (int) Math.round(wind);
        String res = rounded + " м/с";
        return new SpannableString(res);
    }

    public static Spannable pressureUnit(double pressure) {
        int rounded = (int) Math.round(pressure);
        String res = rounded + " мм рт. ст.";
        return new SpannableString(res);
    }

    public static Spannable humidityUnit(double humidity) {
        int rounded = (int) Math.round(humidity);
        String res = rounded + "%";
        return new SpannableString(res);
    }

    public static Spannable sizeSpan(CharSequence text, int textPxSize) {
        if (android.text.TextUtils.isEmpty(text)) {
            return new SpannableString("");
        } else {
            Spannable spannable = new SpannableString(text);
            spannable.setSpan(new AbsoluteSizeSpan(textPxSize), text.length() - 1, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new SuperscriptSpan(), text.length() - 1, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
    }

    public static class HPAtoTorrConverter {
        double input;

        public HPAtoTorrConverter(double input) {
            this.input = input;
        }

        public double convert() {
            return input * 0.750062;
        }
    }

    public static class DegreeToDirection {
        double degree;
        final String[] directions = {
            "северный", "северо-восточный", "восточный", "юго-восточный", "южный", "юго-западный",
            "западный", "северо-западный", "северный"
        };

        public DegreeToDirection(double degree) {
            this.degree = degree;
        }

        public String convert() {
            int index = (int) ((degree + 23) / 45);
            return directions[index];
        }
    }
}
