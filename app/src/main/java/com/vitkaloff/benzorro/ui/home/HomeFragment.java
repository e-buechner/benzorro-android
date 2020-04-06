package com.vitkaloff.benzorro.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.vitkaloff.benzorro.Brand;
import com.vitkaloff.benzorro.Fuel;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.FuelStationAdapter;
import com.vitkaloff.benzorro.MainActivity;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.RetrofitAPI;
import com.vitkaloff.benzorro.Service;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private Double lon;
    private Double lat;

    private static final String FUEL_STATIONS_LIST = "FuelStationList";
    private static final String FUELS_LIST = "FuelsList";
    private static final String BRANDS_LIST = "BrandsList";
    private static final String SERVICES_LIST = "ServicesList";

    private final List<FuelStation> fuelStations = new ArrayList();
    private final List<Brand> brands = new ArrayList();
    private final List<Fuel> fuels = new ArrayList();
    private final List<Service> services = new ArrayList();

    public List<FuelStation> getFuelStations() {
        return fuelStations;
    }
    public List<Brand> getBrands() {
        return brands;
    }
    public List<Fuel> getFuels() {
        return fuels;
    }
    public List<Service> getServices() {
        return services;
    }

    private View.OnClickListener onItemClickListener = view -> {
        //TODO: Step 4 of 4: Finally call getTag() on the view.
        // This viewHolder will have all required values.
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
        int position = viewHolder.getAdapterPosition();
        // viewHolder.getItemId();
        // viewHolder.getItemViewType();
        // viewHolder.itemView;
        FuelStation selectedStation = fuelStations.get(position);
        // Snackbar.make(Objects.requireNonNull(getView()), selectedStation.getAddr(), Snackbar.LENGTH_LONG).show();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putInt("station_position", position);
        bundle.putInt("station_id", selectedStation.getId());
        StationCard stationView = new StationCard();
        stationView.setArguments(bundle);
        fragmentTransaction.replace(this.getId(), stationView);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    };

    private static androidx.fragment.app.FragmentManager fragmentManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fragmentManager = getFragmentManager();

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        RecyclerView recyclerView = root.findViewById(R.id.FuelStationList);
        // создаем адаптер
        FuelStationAdapter adapter = new FuelStationAdapter(this, fuelStations);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);

        Skeleton skeleton;

        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.fuel_list, 25);
        skeleton.setMaskCornerRadius(5);

        skeleton.showSkeleton();

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        ChipGroup chips = root.findViewById(R.id.choice_chip_group);
        chips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        root.findViewById(R.id.choice_chip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        root.findViewById(R.id.choice_chip2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        root.findViewById(R.id.choice_chip3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        root.findViewById(R.id.choice_chip4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        root.findViewById(R.id.choice_chip5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            }
        });

        RetrofitAPI.getApi().getBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(@NotNull Call<List<Brand>> call, @NotNull retrofit2.Response<List<Brand>> response) {
                assert response.body() != null;
                brands.addAll(response.body());
                // Snackbar.make(container, "Данные получены", Snackbar.LENGTH_LONG).show();
                writeBrands(brands);
            }

            @Override
            public void onFailure(@NotNull Call<List<Brand>> call, @NotNull Throwable t) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

        RetrofitAPI.getApi().getFuels().enqueue(new Callback<List<Fuel>>() {
            @Override
            public void onResponse(@NotNull Call<List<Fuel>> call, @NotNull retrofit2.Response<List<Fuel>> response) {
                assert response.body() != null;
                fuels.addAll(response.body());
                // Snackbar.make(container, "Данные получены", Snackbar.LENGTH_LONG).show();
                writeFuels(fuels);
            }

            @Override
            public void onFailure(@NotNull Call<List<Fuel>> call, @NotNull Throwable t) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

        RetrofitAPI.getApi().getServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(@NotNull Call<List<Service>> call, @NotNull retrofit2.Response<List<Service>> response) {
                assert response.body() != null;
                services.addAll(response.body());
                // Snackbar.make(container, "Данные получены", Snackbar.LENGTH_LONG).show();
                writeServices(services);
            }

            @Override
            public void onFailure(@NotNull Call<List<Service>> call, @NotNull Throwable t) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

        View chipGroup = root.findViewById(R.id.choice_chip_group);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int last_dy = 0;

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if ((dy > 0) && (chipGroup.getVisibility() == View.VISIBLE)) {
                    if (last_dy > 0 && dy > 20 || last_dy == 0) chipGroup.setVisibility(View.GONE);
                    last_dy = dy;
                } else if ((dy < 0) && (chipGroup.getVisibility() != View.VISIBLE)) {
                    if (last_dy < 0 && dy < -20 || last_dy == 0) chipGroup.setVisibility(View.VISIBLE);
                    last_dy = dy;
                }
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        lon = location.getLongitude();
                        lat = location.getLatitude();
                    }
                    else {
                        lon = Double.NaN;
                        lat = Double.NaN;
                    }

                    RetrofitAPI.getApi().getStations(lon, lat).enqueue(new Callback<List<FuelStation>>() {
                        @Override
                        public void onResponse(@NotNull Call<List<FuelStation>> call, @NotNull retrofit2.Response<List<FuelStation>> response) {
                            assert response.body() != null;
                            fuelStations.addAll(response.body());
                            // Snackbar.make(container, "Данные получены", Snackbar.LENGTH_LONG).show();
                            adapter.notifyDataSetChanged();
                            writeFuelStations(fuelStations);
                            skeleton.showOriginal();
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<FuelStation>> call, @NotNull Throwable t) {
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
                            skeleton.showOriginal();
                        }
                    });
                });

        adapter.setOnItemClickListener(onItemClickListener);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.objects_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.tuneListButton){
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeFuelStations(List<FuelStation> fuelStations) {
        Gson gson = new Gson();
        String listString = gson.toJson(fuelStations);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(FUEL_STATIONS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(FUEL_STATIONS_LIST, listString);
        editor.apply();
    }

    private void writeBrands(List<Brand> brands) {
        Gson gson = new Gson();
        String listString = gson.toJson(brands);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(BRANDS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(BRANDS_LIST, listString);
        editor.apply();
    }

    private void writeServices(List<Service> services) {
        Gson gson = new Gson();
        String listString = gson.toJson(services);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(SERVICES_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(SERVICES_LIST, listString);
        editor.apply();
    }

    private void writeFuels(List<Fuel> fuels) {
        Gson gson = new Gson();
        String listString = gson.toJson(fuels);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(FUELS_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(FUELS_LIST, listString);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           @NotNull int[] grantResults){
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.permissions_granted), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getText(R.string.permissions_not_granted), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}