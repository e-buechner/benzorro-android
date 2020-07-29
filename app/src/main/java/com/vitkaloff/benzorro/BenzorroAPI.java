package com.vitkaloff.benzorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface BenzorroAPI {

    String versionName = BuildConfig.VERSION_NAME;

    @Headers({"User-Agent: Android/" + versionName})

    @GET("stations/")
    Call<List<FuelStation>> getStations(@Query("lon") Double lon, @Query("lat") Double lat);

    @GET("brands/")
    Call<List<Brand>> getBrands();

    @GET("services/")
    Call<List<Service>> getServices();

    @GET("fuels/")
    Call<List<Fuel>> getFuels();
}