package com.vitkaloff.benzorro.ui.home;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vitkaloff.benzorro.Brand;
import com.vitkaloff.benzorro.Fuel;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.Price;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class StationCard extends Fragment {

    private StationCardViewModel mViewModel;
    private AdView adView;
    private static final String FUEL_STATIONS_LIST = "FuelStationList";
    private static final String FUELS_LIST = "FuelsList";
    private static final String BRANDS_LIST = "BrandsList";
    private static final String SERVICES_LIST = "ServicesList";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.station_card_fragment, container, false);
        setHasOptionsMenu(true);

        List<FuelStation> fuelStations = getFuelStationsFromSharedPrefs();
        List<Brand> brands = getBrandsFromSharedPrefs();
        List<Fuel> fuels = getFuelsFromSharedPrefs();
        List<Service> services = getServicesFromSharedPrefs();


        int station_position = getArguments().getInt("station_position", 0);

        FuelStation station = fuelStations.get(station_position);

        TextView addressView = root.findViewById(R.id.address);
        TextView brandView = root.findViewById(R.id.brand);
        ImageView logoView = root.findViewById(R.id.logo);
        TextView distanceView = root.findViewById(R.id.distance);
        TextView phone = root.findViewById(R.id.phone);
        TextView email = root.findViewById(R.id.email);
        TextView web = root.findViewById(R.id.website);
        TextView app_download = root.findViewById(R.id.app_download);
        TextView price_view = root.findViewById(R.id.Prices);

        addressView.setText(station.getAddr());
        brandView.setText(getBrand(station).getName());
        logoView.setImageResource(station.getLogo());
        BigDecimal distance = BigDecimal.valueOf(station.getDistance() / 1000).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        distanceView.setText(distance.toString() + " км");
        phone.setText(getBrand(station).getPhone());
        email.setText(getBrand(station).getEmail());
        web.setText(getBrand(station).getUrl());

        String play_link = "https://play.google.com/store/apps/details?id=" + getBrand(station).getAndroidAppId();
        play_link = "<a href=" + '"' + play_link + '"' + '>' + getString(R.string.google_play_link) + "</a>";
        app_download.setClickable(true);
        app_download.setMovementMethod(LinkMovementMethod.getInstance());
        app_download.setText(Html.fromHtml(play_link));

        String price_text = "";
        for (Price price : station.getPrices()) {
            Fuel fuel = getFuel(price.getFuel());
            String name;
            if (fuel.getBrandName().equals("")) {
                name = fuel.getType();
            }
            else {
                name = fuel.getBrandName();
            }

            String curr;
            if (price.getCurr().equals("RUB")){
                curr = "₽";
            }
            else{
                curr = price.getCurr();
            }

            price_text = price_text + name + ":   <b>" + price.getPrice().toString() + ' ' + curr + "</b><br><br>";
        }

        if (station.getPrices().size() == 0)
        {
            price_view.setText(R.string.prices_unavailable);
        }
        price_view.setText(Html.fromHtml(price_text));

        FloatingActionButton fab = root.findViewById(R.id.openRouteMap);
        fab.setOnClickListener(v -> {
            Uri geo = Uri.parse("geo:0,0?q=" + station.getLat().toString() + ',' + station.getLon().toString() + "(" + Uri.encode(getBrand(station).getName()) + ")");
            Log.d("@@@@@@@@", geo.toString());
            openMaps(geo);
        });

        FrameLayout adContainerView = root.findViewById(R.id.ad_view_container);
        adView = new AdView(getContext());
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adContainerView.addView(adView);
        loadBanner();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StationCardViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.station_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    private Brand getBrand(FuelStation station) {
        List<Brand> brands = getBrandsFromSharedPrefs();
        int id = station.getBrand();

        for (Brand brand : brands) {
            if (brand.getId() == id) {
                return brand;
            }
        }
        throw new NoSuchElementException();
    }

    private Service getService(Integer id) {
        List<Service> services = getServicesFromSharedPrefs();

        for (Service service : services) {
            if (service.getId().equals(id)) {
                return service;
            }
        }
        throw new NoSuchElementException();
    }

    private Fuel getFuel(Integer id) {
        List<Fuel> fuels = getFuelsFromSharedPrefs();

        for (Fuel fuel : fuels) {
            if (fuel.getId().equals(id)) {
                return fuel;
            }
        }
        throw new NoSuchElementException();
    }

    private void openMaps(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_TEXT, "Выберите карты для продолжения:");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), "Нет приложений для навигации!", Snackbar.LENGTH_LONG).show();
        }
    }

    private List<Service> getServicesFromSharedPrefs(){
        Gson gson = new Gson();
        List<Service> servicesFromShared;
        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(SERVICES_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(SERVICES_LIST, "");

        Type type = new TypeToken<List<Service>>() {}.getType();
        servicesFromShared = gson.fromJson(jsonPreferences, type);

        return servicesFromShared;
    }

    private List<Fuel> getFuelsFromSharedPrefs(){
        Gson gson = new Gson();
        List<Fuel> fuelsFromShared;
        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(FUELS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(FUELS_LIST, "");

        Type type = new TypeToken<List<Fuel>>() {}.getType();
        fuelsFromShared = gson.fromJson(jsonPreferences, type);

        return fuelsFromShared;
    }

    private List<Brand> getBrandsFromSharedPrefs(){
        Gson gson = new Gson();
        List<Brand> brandsFromShared;
        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(BRANDS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(BRANDS_LIST, "");

        Type type = new TypeToken<List<Brand>>() {}.getType();
        brandsFromShared = gson.fromJson(jsonPreferences, type);

        return brandsFromShared;
    }

    private List<FuelStation> getFuelStationsFromSharedPrefs(){
        Gson gson = new Gson();
        List<FuelStation> stationsFromShared;
        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(FUEL_STATIONS_LIST, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(FUEL_STATIONS_LIST, "");

        Type type = new TypeToken<List<FuelStation>>() {}.getType();
        stationsFromShared = gson.fromJson(jsonPreferences, type);

        return stationsFromShared;
    }

    private AdSize getAdSize() {
        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }

    private void loadBanner() {
        // Create an ad request.
        AdRequest adRequest =
                new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }
}
