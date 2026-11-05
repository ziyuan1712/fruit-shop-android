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
import com.example.myapplication.bean.CartItem;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车列表适配器，支持数量增减与删除回调。
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private final List<CartItem> items = new ArrayList<>();
    private final Listener listener;

    public interface Listener {
        void onMinus(CartItem item);

        void onPlus(CartItem item);

        void onDelete(CartItem item);
    }

    public CartAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        CartItem item = items.get(position);
        holder.ivFruit.setImageResource(ResUtil.getDrawableId(holder.itemView.getContext(), item.getImg()));
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("¥" + MoneyUtil.price(item.getPrice()));
        holder.tvUnit.setText("/" + item.getUnit());
        holder.tvCount.setText(String.valueOf(item.getCount()));
        holder.btnMinus.setAlpha(item.getCount() > 1 ? 1f : 0.3f);
        holder.btnMinus.setEnabled(item.getCount() > 1);

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null && item.getCount() > 1) {
                listener.onMinus(item);
            }
        });
        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlus(item);
            }
        });
        holder.btnDel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder {
        final ImageView ivFruit;
        final TextView tvName;
        final TextView tvPrice;
        final TextView tvUnit;
        final TextView tvCount;
        final TextView btnMinus;
        final TextView btnPlus;
        final ImageButton btnDel;

        CartHolder(@NonNull View itemView) {
            super(itemView);
            ivFruit = itemView.findViewById(R.id.iv_cart_fruit);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvUnit = itemView.findViewById(R.id.tv_cart_unit);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDel = itemView.findViewById(R.id.btn_del);
        }
    }
}
