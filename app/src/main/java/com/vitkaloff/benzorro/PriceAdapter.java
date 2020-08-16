package com.vitkaloff.benzorro;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vitkaloff.benzorro.ui.home.StationCard;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Price> prices;
    private SparseArray<Fuel> fuels;
    private SparseArray<Service> services;
    private Integer preferred_fuel_id;
    private String preferred_fuel_type;
    private View.OnClickListener mOnItemClickListener;

    public PriceAdapter(StationCard context, List<Price> prices) {
        this.prices = prices;
        this.inflater = LayoutInflater.from(context.getActivity());

        fuels = SharedData.getFuels(inflater.getContext());
        services = SharedData.getServices(inflater.getContext());
        preferred_fuel_id = SharedData.getPreferredFuelId(inflater.getContext());
        preferred_fuel_type = fuels.get(preferred_fuel_id).getType();

    }
    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {

        View view = inflater.inflate(R.layout.price_list, container, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int station) {
        if (fuels.get(prices.get(station).getFuel()).getBrand()==0) {
            holder.fuelView.setText(fuels.get(prices.get(station).getFuel()).getType());
        }
        else {
            holder.fuelView.setText(fuels.get(prices.get(station).getFuel()).getBrandName());
        }

        if(fuels.get(prices.get(station).getFuel()).getType().equals(preferred_fuel_type)){
            holder.fuelView.setTypeface(null, Typeface.BOLD);
        }

        holder.priceChangeView.setImageResource(R.drawable.ic_arrow_down_24);
        holder.priceView.setText(prices.get(station).getPrice() + " \u20BD");
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView priceChangeView;
        final TextView fuelView, priceView;
        ViewHolder(View view){
            super(view);
            fuelView = view.findViewById(R.id.fuelView);
            priceChangeView = view.findViewById(R.id.priceChangeView);
            priceView = view.findViewById(R.id.priceView);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}