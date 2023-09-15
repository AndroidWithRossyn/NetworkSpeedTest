package com.networkdigitally.analyzertools.speedtool.activity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface InterFaceAPI {
    @GET("data/2.5/weather")
    Call<MausamData> getData(
            @Query("q") String q,
            @Query("appid") String APIKEY,
            @Query("units") String units

    );
}
