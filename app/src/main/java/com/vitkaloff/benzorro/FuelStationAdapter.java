package com.vitkaloff.benzorro;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.vitkaloff.benzorro.ui.home.HomeFragment;

import java.math.BigDecimal;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class FuelStationAdapter extends RecyclerView.Adapter<FuelStationAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<FuelStation> fuelStationList;
    private SparseArray<Brand> brands;
    private Price price;
    private View.OnClickListener mOnItemClickListener;


    public FuelStationAdapter(HomeFragment fragment, List<FuelStation> fuelStationList) {
        this.fuelStationList = fuelStationList;
        this.inflater = LayoutInflater.from(fragment.getActivity());
    }
    @Override
    public FuelStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.station_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FuelStationAdapter.ViewHolder holder, int id) {
        brands = SharedData.getBrands(inflater.getContext());
        FuelStation fuelstation = fuelStationList.get(id);
        Brand brand = brands.get(fuelstation.getBrand());
        String image_url = brand.getLogo();
        Glide.with(inflater.getContext())
                .load(image_url)
                .placeholder(R.drawable.ic_baseline_station_24)
                .into(holder.imageView);
        //holder.imageView.setImageResource(R.drawable.brand_1);
        holder.brandView.setText(brand.getName());
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
        return fuelStationList.size();
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
            priceView = view.findViewById(R.id.priceView);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}