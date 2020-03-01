package com.vitkaloff.benzorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import com.vitkaloff.benzorro.BuildConfig;

public interface BenzorroAPI {

    String versionName = BuildConfig.VERSION_NAME;

    @Headers({"User-Agent: Android/" + versionName})

    @GET("stations/")
    Call<List<FuelStation>> getStations(@Query("lon") Double lon, @Query("lat") Double lat);


}
