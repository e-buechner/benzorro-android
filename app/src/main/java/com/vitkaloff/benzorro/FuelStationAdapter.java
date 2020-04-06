package com.vitkaloff.benzorro;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vitkaloff.benzorro.ui.home.HomeFragment;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

import static java.security.AccessController.getContext;

public class FuelStationAdapter extends RecyclerView.Adapter<FuelStationAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<FuelStation> fuelStations;
    private Price price;
    private View.OnClickListener mOnItemClickListener;

    private static final String FUELS_LIST = "FuelsList";
    private static final String BRANDS_LIST = "BrandsList";
    private static final String SERVICES_LIST = "ServicesList";

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
        BigDecimal distance = BigDecimal.valueOf(fuelstation.getDistance() / 1000).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        holder.distanceView.setText(distance +" км");
        String priceStr;
        try {
            priceStr = fuelstation.getPrices().get(0).getPrice().toString();
        }
        catch (IndexOutOfBoundsException e)
        {
            priceStr = "-";
        }
        holder.priceView.setText(priceStr + " \u20BD");
    }

    @Override
    public int getItemCount() {
        return fuelStations.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
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

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}