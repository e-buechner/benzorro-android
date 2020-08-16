package com.vitkaloff.benzorro.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Explode;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.vitkaloff.benzorro.Brand;
import com.vitkaloff.benzorro.Fuel;
import com.vitkaloff.benzorro.FuelStation;
import com.vitkaloff.benzorro.Price;
import com.vitkaloff.benzorro.R;
import com.vitkaloff.benzorro.SharedData;
import com.vitkaloff.benzorro.ui.home.StationCard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    SparseArray<Brand> brands;
    SparseArray<FuelStation> fuelStations;
    SparseArray<Fuel> fuels;
    List<Price> prices;
    List<Marker> markers = new ArrayList<>();
    String preferred_fuel_type;
    Marker selected_marker;
    BitmapDescriptor marker_icon;
    LinearLayout station_card;
    View station_card_content;
    int preferred_fuel_id;
    int alpha_value = 230;

    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(this);
        ChipGroup chipGroup = requireActivity().findViewById(R.id.fuel_switcher);
        Chip chip_holder = new Chip(requireContext());
        chip_holder.setVisibility(View.INVISIBLE);
        chipGroup.addView(chip_holder);

        int null_id = fuelStations.keyAt(0);
        FuelStation null_station = fuelStations.get(null_id);
        LatLng null_point = new LatLng(null_station.getLat(), null_station.getLon());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(null_point, 10));

        for (int i = 0; i < fuels.size(); i++) {
            int key = fuels.keyAt(i);
            Fuel fuel = fuels.get(key);
            if (fuel.getBrand() == 0) {
                Chip chip = new Chip(requireContext());
                chip.setId(ViewCompat.generateViewId());
                chip.setText(fuel.getBrandName());
                chip.setHint(fuel.getId().toString());
                chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                chip.setCheckable(true);
                chip.getBackground().setAlpha(alpha_value);

                chipGroup.addView(chip);

                chip.setOnClickListener(v -> {
                    preferred_fuel_id = Integer.parseInt(chip.getHint().toString());
                    SharedData.setPreferredFuelId(requireContext(), preferred_fuel_id);
                    preferred_fuel_type = fuels.get(preferred_fuel_id).getType();
                    chip.setChecked(true);

                    if(selected_marker!=null) {
                        this.onMarkerClick(selected_marker);
                        selected_marker.setAlpha(1);
                    }

                    if (markers.size() > 0){
                        for (int m=0;m<markers.size();m++) {
                            Marker marker = markers.get(m);
                            Integer station_id = (Integer) marker.getTag();
                            FuelStation station = fuelStations.get(station_id);
                            boolean matches = false;

                            for (int k = 0; k < station.getPrices().size(); k++) {
                                Fuel fuel1 = fuels.get(station.getPrices().get(k).getFuel());
                                String fuel_type = fuel1.getType();
                                if (fuel_type.contentEquals(preferred_fuel_type)) {
                                    matches = true;
                                    break;
                                }
                            }

                            if(matches){
                                marker.setAlpha(1);
                            } else {
                                marker.setAlpha(0.20f);
                            }
                        }
                    } else {
                        for (int j = 0; j < fuelStations.size(); j++) {
                            int key2 = fuelStations.keyAt(j);
                            FuelStation station = fuelStations.get(key2);
                            Brand brand = brands.get(station.getBrand());
                            LatLng point = new LatLng(station.getLat(), station.getLon());
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(point));
                            marker.setIcon(marker_icon);
                            marker.setTag(station.getId());
                            googleMap.setOnMarkerClickListener(this);
                            markers.add(marker);
                        }
                    }
                });

                if (preferred_fuel_id == fuel.getId()) {
                    chip.setChecked(true);
                    chip.callOnClick();
                }
            }
        }
        chipGroup.removeView(chip_holder);
        chipGroup.setSingleSelection(true);

        googleMap.setTrafficEnabled(true);
        UiSettings map_ui = googleMap.getUiSettings();
        map_ui.setZoomControlsEnabled(true);
        map_ui.setMyLocationButtonEnabled(true);
        map_ui.setMapToolbarEnabled(true);
        googleMap.setMinZoomPreference(5);
        googleMap.setMaxZoomPreference(20);
    }

    public boolean onMarkerClick(final Marker marker) {
        if(selected_marker!=null){
            selected_marker.setIcon(marker_icon);
        } else {
            selected_marker = marker;
        }

        station_card_content.setOnClickListener(this::onStationCardClick);
        selected_marker = marker;
        selected_marker.setIcon(null);

        Integer station_id = (Integer) marker.getTag();
        FuelStation station = fuelStations.get(station_id);
        Brand brand = brands.get(station.getBrand());

        TextView addressView = getActivity().findViewById(R.id.address);
        addressView.setText(station.getAddr());

        TextView brandView = getActivity().findViewById(R.id.brand);
        brandView.setText(brand.getName());

        ImageView logoView = getActivity().findViewById(R.id.logo);
        Glide.with(getContext()).load(brand.getLogo()).into(logoView);

        TextView distanceView = getActivity().findViewById(R.id.distance);
        BigDecimal distance = BigDecimal.valueOf(station.getDistance() / 1000).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        distanceView.setText(distance.toString() + " км");

        TextView priceView = getActivity().findViewById(R.id.priceView);

        String priceStr;
        prices = station.getPrices();

        Double min_price = 0.0;
        try {
            for (int i=0; i<prices.size(); i++ ) {
                if(fuels.get(prices.get(i).getFuel()).getType().equals(preferred_fuel_type)){
                    double curr_price = prices.get(i).getPrice();
                    if(min_price != 0.0){
                        min_price = curr_price;
                    }
                    if(curr_price > min_price){
                        min_price = curr_price;
                    }
                }
            }
            priceStr = min_price.toString();
            if(min_price==0.0){
                priceStr = "-";
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            priceStr = "-";
        }
        priceView.setText(priceStr + " \u20BD");

        station_card_content.setVisibility(View.VISIBLE);
        station_card.setVisibility(View.VISIBLE);
        return false;
    }

    public void onMapClick(LatLng latLng) {
        selected_marker.setIcon(marker_icon);
        station_card.setVisibility(View.GONE);
        station_card_content.setVisibility(View.GONE);
    }

    public void onStationCardClick(View view) {
        FragmentManager fragmentManager = this.getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putInt("station_id", (Integer) selected_marker.getTag());
        StationCard stationView = new StationCard();

        //fragmentTransaction.addSharedElement(viewHolder.itemView.findViewById(R.id.logo), "stationCardBrandLogo");
        //fragmentTransaction.addSharedElement(viewHolder.itemView.findViewById(R.id.address), "stationCardAddress");
        //fragmentTransaction.addSharedElement(viewHolder.itemView.findViewById(R.id.brand), "stationCardBrand);

        setExitTransition(new Explode());
        stationView.setArguments(bundle);
        fragmentTransaction.replace(this.getId(), stationView);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        fuelStations = SharedData.getStations(requireContext());
        brands = SharedData.getBrands(requireContext());
        fuels = SharedData.getFuels(requireContext());
        preferred_fuel_id = SharedData.getPreferredFuelId(requireContext());

        station_card_content = getLayoutInflater().inflate(R.layout.station_list, container, false);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        station_card = getActivity().findViewById(R.id.map_station_card);
        station_card.addView(station_card_content);
        station_card_content.setPadding(25, 25,25,25);

        station_card.getBackground().setAlpha(alpha_value);
        getActivity().findViewById(R.id.fuels_scroll).getBackground().setAlpha(alpha_value);

        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_red_dot_24);
        marker_icon = getMarkerIconFromDrawable(circleDrawable);
    }

    private BitmapDescriptor getMarkerIconFromDrawable (Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}