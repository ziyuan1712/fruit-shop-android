package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.FavoriteItem;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏列表适配器。
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
    private final List<FavoriteItem> items = new ArrayList<>();
    private final Listener listener;

    public interface Listener {
        void onFavoriteClick(FavoriteItem item);

        void onFavoriteRemove(FavoriteItem item);
    }

    public FavoriteAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<FavoriteItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        FavoriteItem item = items.get(position);
        holder.ivImage.setImageResource(ResUtil.getDrawableId(holder.itemView.getContext(), item.getImg()));
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDesc());
        holder.tvPrice.setText(MoneyUtil.price(item.getPrice()));
        holder.tvUnit.setText("/" + item.getUnit());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(item);
            }
        });
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteRemove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavoriteHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvName;
        final TextView tvDesc;
        final TextView tvPrice;
        final TextView tvUnit;
        final ImageButton btnRemove;

        FavoriteHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_favorite);
            tvName = itemView.findViewById(R.id.tv_favorite_name);
            tvDesc = itemView.findViewById(R.id.tv_favorite_desc);
            tvPrice = itemView.findViewById(R.id.tv_favorite_price);
            tvUnit = itemView.findViewById(R.id.tv_favorite_unit);
            btnRemove = itemView.findViewById(R.id.btn_remove_favorite);
        }
    }
}
