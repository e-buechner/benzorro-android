package com.vitkaloff.benzorro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.snackbar.Snackbar;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.FuelStationAdapter;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.RetrofitAPI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.navigation_about);

        final List<FuelStation> fuelStations = new ArrayList();

        super.onCreate(savedInstanceState);

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

        RetrofitAPI.getApi().getStations(30.295386235846195, 59.97306639988085).enqueue(new Callback<List<FuelStation>>() {
            @Override
            public void onResponse(@NotNull Call<List<FuelStation>> call, @NotNull retrofit2.Response<List<FuelStation>> response) {
                assert response.body() != null;
                fuelStations.addAll(response.body());
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.FuelStationList),
                        "Данные получены", Snackbar.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
                skeleton.showOriginal();
            }

            @Override
            public void onFailure(@NotNull Call<List<FuelStation>> call, @NotNull Throwable t) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.FuelStationList), t.toString(), Snackbar.LENGTH_LONG).show();
                skeleton.showOriginal();
            }
        });
        return root;
    }
}