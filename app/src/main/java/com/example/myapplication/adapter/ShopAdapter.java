package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.ResUtil;

import java.util.List;
import java.util.Locale;

/**
 * 首页店铺列表适配器。
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopHolder> {
    private final List<Shop> shopList;
    private final OnShopClick listener;

    public interface OnShopClick {
        void onShopClick(Shop shop);
    }

    public ShopAdapter(List<Shop> shopList, OnShopClick listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopHolder holder, int position) {
        Shop shop = shopList.get(position);
        holder.ivShop.setImageResource(ResUtil.getDrawableId(holder.itemView.getContext(), shop.getShopImg()));
        holder.tvName.setText(shop.getShopName());
        holder.tvDesc.setText(shop.getShopDesc());
        holder.tvRating.setText(String.format(Locale.CHINA, "★ %.1f", shop.getRating()));
        String delivery = shop.getDeliveryTime();
        holder.tvDelivery.setText(delivery.endsWith("达") ? delivery : delivery + "达");
        holder.tvDistance.setText(shop.getDistance());
        String tags = shop.getTags() == null ? "" : shop.getTags().replace(",", " · ");
        if (tags.isEmpty()) {
            tags = "起送 " + MoneyUtil.amount(shop.getMinOrder());
        }
        holder.tvTags.setText(tags);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShopClick(shop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList == null ? 0 : shopList.size();
    }

    static class ShopHolder extends RecyclerView.ViewHolder {
        final ImageView ivShop;
        final TextView tvName;
        final TextView tvDesc;
        final TextView tvRating;
        final TextView tvDelivery;
        final TextView tvDistance;
        final TextView tvTags;

        ShopHolder(@NonNull View itemView) {
            super(itemView);
            ivShop = itemView.findViewById(R.id.iv_shop);
            tvName = itemView.findViewById(R.id.tv_shop_name);
            tvDesc = itemView.findViewById(R.id.tv_shop_desc);
            tvRating = itemView.findViewById(R.id.tv_shop_rating);
            tvDelivery = itemView.findViewById(R.id.tv_shop_delivery);
            tvDistance = itemView.findViewById(R.id.tv_shop_distance);
            tvTags = itemView.findViewById(R.id.tv_shop_tags);
        }
    }
}
