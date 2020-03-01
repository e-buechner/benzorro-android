package com.vitkaloff.benzorro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.snackbar.Snackbar;
import com.vitkaloff.benzorro.BenzorroAPI;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.FuelStationAdapter;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.RetrofitAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        // получаем элемент ListView

        ListView fuelStationList = (ListView) root.findViewById(R.id.FuelStationList);
        // создаем адаптер
        FuelStationAdapter adapter = new FuelStationAdapter(this.getContext(), R.layout.fuel_list, fuelStations);
        // устанавливаем адаптер
        fuelStationList.setAdapter(adapter);

        SkeletonScreen skeletonScreen;

        skeletonScreen = Skeleton.bind(fuelStationList)
                .load(R.layout.fuel_list)
                .duration(1000)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();

        RetrofitAPI.getApi().getStations(30.295386235846195, 59.97306639988085).enqueue(new Callback<List<FuelStation>>() {
            @Override
            public void onResponse(Call<List<FuelStation>> call, retrofit2.Response<List<FuelStation>> response) {
                fuelStations.addAll(response.body());
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Данные получены", Snackbar.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
                skeletonScreen.hide();
            }

            @Override
            public void onFailure(Call<List<FuelStation>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show();
                skeletonScreen.hide();
            }
        });

        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                FuelStation selectedStation = (FuelStation) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Был выбран пункт " + ' ' + position,
                        Toast.LENGTH_SHORT).show();
            }
        };
        fuelStationList.setOnItemClickListener(itemListener);
        return root;
    }

    public class ExampleRequest {
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }

    }
}