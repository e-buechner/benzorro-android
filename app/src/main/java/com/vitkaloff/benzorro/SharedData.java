package com.vitkaloff.benzorro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedData {
    private static final String STATIONS_LIST = "StationsList";
    private static final String FUELS_LIST = "FuelsList";
    private static final String BRANDS_LIST = "BrandsList";
    private static final String SERVICES_LIST = "ServicesList";
    private static final String PREFERRED_FUEL_ID = "PreferredFuelId";

    public static SparseArray<FuelStation> getStations(Context context){
        Gson gson = new Gson();
        List<FuelStation> objects;
        SharedPreferences sharedPref = context.getSharedPreferences(STATIONS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(STATIONS_LIST, "");

        Type type = new TypeToken<List<FuelStation>>() {}.getType();
        objects = gson.fromJson(jsonPreferences, type);
        SparseArray<FuelStation> array = new SparseArray<FuelStation>();

        for (int i=0; i<objects.size(); i++ ) {
            array.put(objects.get(i).getId(), objects.get(i));
        }
        return array;
    }
    public static SparseArray<Brand> getBrands(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(BRANDS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(BRANDS_LIST, "");

        Type type = new TypeToken<List<Brand>>() {}.getType();
        List<Brand> objects = gson.fromJson(jsonPreferences, type);
        SparseArray<Brand> array = new SparseArray<Brand>();

        for (int i=0; i<objects.size(); i++ ) {
            array.put(objects.get(i).getId(), objects.get(i));
        }
        return array;
    }
    public static SparseArray<Service> getServices(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(SERVICES_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(SERVICES_LIST, "");

        Type type = new TypeToken<List<Service>>() {}.getType();
        List<Service> objects = gson.fromJson(jsonPreferences, type);
        SparseArray<Service> array = new SparseArray<Service>();

        for (int i=0; i<objects.size(); i++ ) {
            array.put(objects.get(i).getId(), objects.get(i));
        }
        return array;
    }
    public static SparseArray<Fuel> getFuel(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(FUELS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(FUELS_LIST, "");

        Type type = new TypeToken<List<Fuel>>() {}.getType();
        List<Fuel> objects = gson.fromJson(jsonPreferences, type);
        SparseArray<Fuel> array = new SparseArray<Fuel>();

        for (int i=0; i<objects.size(); i++ ) {
            array.put(objects.get(i).getId(), objects.get(i));
        }
        return array;
    }
    public static Integer getPreferredFuelId(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERRED_FUEL_ID, Context.MODE_PRIVATE);
        String value = sharedPref.getString(PREFERRED_FUEL_ID, "0");
        return Integer.parseInt(value);
    }

    public static void setPreferredFuelId(Context context, Integer fuel_id) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERRED_FUEL_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(PREFERRED_FUEL_ID, fuel_id.toString());
        editor.apply();
        editor.commit();
    }
    public static void writeStations(Context context, List<FuelStation> fuelStations) {
        Gson gson = new Gson();
        String listString = gson.toJson(fuelStations);

        SharedPreferences sharedPref = context.getSharedPreferences(STATIONS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(STATIONS_LIST, listString);
        editor.apply();
    }
    public static void writeBrands(Context context, List<Brand> brands) {
        Gson gson = new Gson();
        String listString = gson.toJson(brands);

        SharedPreferences sharedPref = context.getSharedPreferences(BRANDS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(BRANDS_LIST, listString);
        editor.apply();
    }
    public static void writeServices(Context context, List<Service> services) {
        Gson gson = new Gson();
        String listString = gson.toJson(services);

        SharedPreferences sharedPref = context.getSharedPreferences(SERVICES_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(SERVICES_LIST, listString);
        editor.apply();
    }
    public static void writeFuels(Context context, List<Fuel> fuels) {
        Gson gson = new Gson();
        String listString = gson.toJson(fuels);

        SharedPreferences sharedPref = context.getSharedPreferences(FUELS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(FUELS_LIST, listString);
        editor.apply();
    }
}

