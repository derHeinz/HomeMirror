package com.morristaedt.mirror.requests;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by HannahMitt on 8/23/15.
 */
public interface ForecastRequest {

    String UNITS_SI = "si";
    String UNITS_US = "us";

    //@GET("/forecast/{apikey}/{lat},{lon}")
    @GET("/weather")
    ForecastResponse getHourlyForecast(@Query("appid") String apiKey, @Query("lat") String lat, @Query("lon") String lon);
}
