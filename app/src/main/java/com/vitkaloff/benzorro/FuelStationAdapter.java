package com.vitkaloff.benzorro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitkaloff.benzorro.ui.home.HomeFragment;
import com.vitkaloff.benzorro.Price;

import java.math.BigDecimal;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class FuelStationAdapter extends RecyclerView.Adapter<FuelStationAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<FuelStation> fuelStations;
    private Price price;

    public FuelStationAdapter(HomeFragment fragment, List<FuelStation> fuelStations) {
        this.fuelStations = fuelStations;
        this.inflater = LayoutInflater.from(fragment.getActivity());
    }
    @Override
    public FuelStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.fuel_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FuelStationAdapter.ViewHolder holder, int position) {
        FuelStation fuelstation = fuelStations.get(position);
        holder.imageView.setImageResource(fuelstation.getLogo());
        holder.imageView.setImageResource(fuelstation.getLogo());
        holder.brandView.setText("Газпромнефть");
        holder.addressView.setText(fuelstation.getAddr());
        BigDecimal distance = BigDecimal.valueOf(fuelstation.getDistance() / 1000).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        holder.distanceView.setText(distance +" км");
        holder.priceView.setText(0 + " \u20BD");
    }

    @Override
    public int getItemCount() {
        return fuelStations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView brandView, addressView, distanceView, priceView;
        ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.logo);
            brandView = view.findViewById(R.id.brand);
            addressView = view.findViewById(R.id.address);
            distanceView = view.findViewById(R.id.distance);
            priceView = view.findViewById(R.id.price);
        }
    }
}