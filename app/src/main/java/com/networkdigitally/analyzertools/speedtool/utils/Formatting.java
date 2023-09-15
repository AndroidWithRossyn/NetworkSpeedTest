package com.networkdigitally.analyzertools.speedtool.utils;

import android.content.Context;

import com.networkdigitally.analyzertools.speedtool.utils.formatters.WeatherFormatter;



public class Formatting {

    private Context context;

    public Formatting(Context context) {
        this.context = context;
    }

    public String getWeatherIcon(int actualId, boolean isDay) {
        return WeatherFormatter.getWeatherIconAsText(actualId, isDay, context);
    }
}
