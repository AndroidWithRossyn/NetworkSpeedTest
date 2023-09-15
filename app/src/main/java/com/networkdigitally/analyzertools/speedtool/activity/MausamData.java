package com.networkdigitally.analyzertools.speedtool.activity;

import java.util.List;

public class MausamData {
    private static List<weather> weather;
    private main main;
    private String name;

    public MausamData(List<com.networkdigitally.analyzertools.speedtool.activity.weather> weather, com.networkdigitally.analyzertools.speedtool.activity.main main, String name) {
        this.weather = weather;
        this.main = main;
        this.name = name;
    }

    public static List<weather> getWeather() {
        return weather;
    }

    public void setWeather(List<com.networkdigitally.analyzertools.speedtool.activity.weather> weather) {
        this.weather = weather;
    }

    public com.networkdigitally.analyzertools.speedtool.activity.main getMain() {
        return main;
    }

    public void setMain(com.networkdigitally.analyzertools.speedtool.activity.main main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
