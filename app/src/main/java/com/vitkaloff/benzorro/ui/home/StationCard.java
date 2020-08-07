package com.vitkaloff.benzorro.ui.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vitkaloff.benzorro.Brand;
import com.vitkaloff.benzorro.Fuel;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.PriceAdapter;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.Service;
import com.vitkaloff.benzorro.SharedData;

import java.math.BigDecimal;
import java.util.List;

public class StationCard extends Fragment {

    private StationCardViewModel mViewModel;
    private AdView adView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.station_card_fragment, container, false);
        setHasOptionsMenu(true);

        SparseArray<FuelStation> fuelStations = SharedData.getStations(requireActivity());
        SparseArray<Brand> brands = SharedData.getBrands(requireActivity());
        SparseArray<Fuel> fuels = SharedData.getFuel(requireActivity());
        SparseArray<Service> services = SharedData.getServices(requireActivity());

        int station_id = getArguments().getInt("station_id", 0);
        boolean is_installed = false;
        PackageManager pm = getActivity().getPackageManager();

        FuelStation station = fuelStations.get(station_id);
        Brand brand = brands.get(station.getBrand());

        TextView addressView = root.findViewById(R.id.address);
        addressView.setText(station.getAddr());

        TextView brandView = root.findViewById(R.id.brand);
        brandView.setText(brand.getName());

        ImageView logoView = root.findViewById(R.id.logo);
        Glide.with(this).load(brand.getLogo()).into(logoView);

        TextView distanceView = root.findViewById(R.id.distance);
        BigDecimal distance = BigDecimal.valueOf(station.getDistance() / 1000).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        distanceView.setText(distance.toString() + " км");

        ChipGroup service_group = root.findViewById(R.id.services_group);
        List<Integer> services_list = station.getServices();

        for (int i=0; i<services_list.size(); i++ ) {
            Chip chip = new Chip(getActivity());
            String label = services.get(services_list.get(i)).getLogo();
            setServiceChip(chip, service_group, label);
        }

        RecyclerView price_list = (RecyclerView) root.findViewById(R.id.priceList);
        PriceAdapter adapter = new PriceAdapter(this, station.getPrices());
        price_list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        price_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button open_app = root.findViewById(R.id.open_app);
        String app_id = brand.getAndroidAppId();
        if (app_id.equals("")) {
            open_app.setVisibility(View.GONE);
        } else {
            if (isPackageInstalled(app_id, pm)) {
                    open_app.setText("Открыть приложение");
            } else {
                open_app.setText("Установить приложение");
            }
        }
        open_app.setOnClickListener(v -> {
            if (isPackageInstalled(app_id, pm)) {
                Intent launchIntent = pm.getLaunchIntentForPackage(app_id);
                getActivity().startActivity(launchIntent);
            } else {
                openPlayStore(getActivity(), app_id);
            }
        });

        Button call = root.findViewById(R.id.call);
        call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", brands.get(station.getBrand()).getPhone(), null));
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Приложения для звонков не найдены!\nЗвоните по номеру " + brand.getPhone(), Snackbar.LENGTH_LONG).show();
            }
        });

        Button mail = root.findViewById(R.id.mail);
        mail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + brand.getEmail())); // only email apps should handle this
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Почтовые приложения не найдены!\nОтправьте письмо по адресу " + brand.getEmail(), Snackbar.LENGTH_LONG).show();
            }
        });

        Button web = root.findViewById(R.id.web);
        web.setOnClickListener(v -> {
            Uri webpage = Uri.parse(brand.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Для просмотра сайта установите браузер\n" + brand.getUrl(), Snackbar.LENGTH_LONG).show();
            }
        });

        // событие нажатия кнопки навигации
        FloatingActionButton fab = root.findViewById(R.id.openRouteMap);
        fab.setOnClickListener(v -> {
            Uri geo = Uri.parse("geo:0,0?q=" + station.getLat().toString() + ',' + station.getLon().toString() + "(" + Uri.encode(brand.getName() + ")"));
            openMaps(geo);
        });

        // инициализируем рекламу
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
        Snackbar.make(requireActivity().findViewById(android.R.id.content), getText(R.string.not_implemented), Snackbar.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openPlayStore(Context context, String app_id) {
        Intent storeIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + app_id));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(storeIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                storeIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                storeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                storeIntent.setComponent(componentName);
                context.startActivity(storeIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+app_id));
            context.startActivity(webIntent);
        }
    }

    private void setServiceChip(Chip chip, ChipGroup group, String label) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        int sp = (int)  (getResources().getDimensionPixelSize(R.dimen.min_sp) / scale * 2);

        chip.setLayoutParams(new ViewGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT));
        chip.setClickable(false);
        chip.setTextColor(getResources().getColor(R.color.design_default_color_background));
        chip.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_background)));
        chip.setChipStrokeWidth(sp);
        chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        chip.setText(label);
        group.addView(chip);
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
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Нет приложений для навигации!", Snackbar.LENGTH_LONG).show();
        }
    }

    private AdSize getAdSize() {
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }
}
