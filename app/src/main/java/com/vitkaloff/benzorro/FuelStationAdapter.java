package com.vitkaloff.benzorro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.math.*;

public class FuelStationAdapter extends ArrayAdapter<FuelStation> {

    private LayoutInflater inflater;
    private int layout;
    private List<FuelStation> fuelStations;

    public FuelStationAdapter(Context context, int resource, List<FuelStation> fuelStations){
        super(context, resource, fuelStations);
        this.fuelStations = fuelStations;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View ConvertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        ImageView logoView = (ImageView) view.findViewById(R.id.logo);
        TextView brandView = (TextView) view.findViewById(R.id.brand);
        TextView addressView = (TextView) view.findViewById(R.id.address);
        TextView distanceView = (TextView) view.findViewById(R.id.distance);
        TextView priceView = (TextView) view.findViewById(R.id.price);

        FuelStation fuelStation = fuelStations.get(position);

        logoView.setImageResource(fuelStation.getLogo());
        // brandView.setText(fuelStation.getBrand().toString());
        brandView.setText("Газпромнефть");
        addressView.setText(fuelStation.getAddr());
        BigDecimal distance = BigDecimal.valueOf(fuelStation.getDistance().doubleValue() / 1000).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        distanceView.setText(distance +" км");
        priceView.setText(0 + " \u20BD");

        return view;
    }
}