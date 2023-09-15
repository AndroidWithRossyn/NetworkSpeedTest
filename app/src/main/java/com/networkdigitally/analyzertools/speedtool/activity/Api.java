package com.networkdigitally.analyzertools.speedtool.activity;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("json")
    Call<wModel> getMethod();
}