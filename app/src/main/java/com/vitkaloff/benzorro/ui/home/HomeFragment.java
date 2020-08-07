package com.vitkaloff.benzorro.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.vitkaloff.benzorro.Brand;
import com.vitkaloff.benzorro.Fuel;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.FuelStationAdapter;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.RetrofitAPI;
import com.vitkaloff.benzorro.Service;
import com.vitkaloff.benzorro.SharedData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.res.ColorStateList.*;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private Double lon;
    private Double lat;

    private List<FuelStation> fuelStationList = new ArrayList<>();
    private final List<Brand> brands = new ArrayList();
    private final List<Fuel> fuels = new ArrayList();
    private final List<Service> services = new ArrayList();

    private View.OnClickListener onItemClickListener = view -> {
        //TODO: Step 4 of 4: Finally call getTag() on the view.
        // This viewHolder will have all required values.
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
        int position = viewHolder.getAdapterPosition();
        FuelStation selectedStation = fuelStationList.get(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
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
        FuelStationAdapter adapter = new FuelStationAdapter(this, fuelStationList);
        recyclerView.setAdapter(adapter);

        Skeleton skeleton;
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.station_list, 25);
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

        ChipGroup chipGroup = root.findViewById(R.id.services);
        Chip chip_holder = new Chip(getContext());
        chip_holder.setVisibility(View.INVISIBLE);
        chipGroup.addView(chip_holder);

        RetrofitAPI.getApi().getFuels().enqueue(new Callback<List<Fuel>>() {
            @Override
            public void onResponse(@NotNull Call<List<Fuel>> call, @NotNull retrofit2.Response<List<Fuel>> response) {
                assert response.body() != null;
                fuels.clear();
                fuels.addAll(response.body());

                int preferred_id = SharedData.getPreferredFuelId(getContext());

                for (int i=0; i<fuels.size(); i++ ) {
                    Fuel fuel = fuels.get(i);
                    if(fuel.getBrand()==0) {
                        Chip chip = new Chip(getContext());
                        chip.setId(ViewCompat.generateViewId());
                        chip.setText(fuel.getType());
                        chip.setHint(fuel.getId().toString());
                        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        chip.setCheckable(true);

                        if(preferred_id==fuel.getId()){
                            chip.setChecked(true);
                        }

                        chipGroup.addView(chip);

                        chip.setOnClickListener(v -> {
                            SharedData.setPreferredFuelId(getContext(), Integer.parseInt(chip.getHint().toString()));
                            chip.setChecked(true);
                            adapter.notifyDataSetChanged();
                        });
                    }
                }
                chipGroup.removeView(chip_holder);
                chipGroup.setSingleSelection(true);
                SharedData.writeFuels(requireActivity(), fuels);

                if(SharedData.getPreferredFuelId(requireContext())==0){
                    SharedData.setPreferredFuelId(requireActivity(), fuels.get(0).getId());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Fuel>> call, @NotNull Throwable t) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
        RetrofitAPI.getApi().getBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(@NotNull Call<List<Brand>> call, @NotNull retrofit2.Response<List<Brand>> response) {
                assert response.body() != null;
                brands.clear();
                brands.addAll(response.body());
                SharedData.writeBrands(requireActivity(), brands);
            }

            @Override
            public void onFailure(@NotNull Call<List<Brand>> call, @NotNull Throwable t) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
        RetrofitAPI.getApi().getServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(@NotNull Call<List<Service>> call, @NotNull retrofit2.Response<List<Service>> response) {
                assert response.body() != null;
                services.clear();
                services.addAll(response.body());
                SharedData.writeServices(requireActivity(), services);
            }

            @Override
            public void onFailure(@NotNull Call<List<Service>> call, @NotNull Throwable t) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
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
                            fuelStationList.clear();
                            fuelStationList.addAll(response.body());

                            adapter.notifyDataSetChanged();
                            SharedData.writeStations(getActivity(), fuelStationList);
                            skeleton.showOriginal();
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<FuelStation>> call, @NotNull Throwable t) {
                            Snackbar.make(requireActivity().findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           @NotNull int[] grantResults){
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), getText(R.string.permissions_granted), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), getText(R.string.permissions_not_granted), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}