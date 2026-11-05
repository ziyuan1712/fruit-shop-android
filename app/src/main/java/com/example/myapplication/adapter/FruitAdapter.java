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
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.ResUtil;

import java.util.List;

/**
 * 店铺详情页水果列表适配器（两列网格）。
 */
public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitHolder> {
    private final List<Fruit> fruitList;
    private final OnFruitClick clickListener;
    private final OnAddCart addListener;

    public interface OnFruitClick {
        void onFruitClick(Fruit fruit);
    }

    public interface OnAddCart {
        void onAddCart(Fruit fruit);
    }

    public FruitAdapter(List<Fruit> fruitList, OnFruitClick clickListener, OnAddCart addListener) {
        this.fruitList = fruitList;
        this.clickListener = clickListener;
        this.addListener = addListener;
    }

    @NonNull
    @Override
    public FruitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fruit, parent, false);
        return new FruitHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitHolder holder, int position) {
        Fruit fruit = fruitList.get(position);
        holder.ivFruit.setImageResource(ResUtil.getDrawableId(holder.itemView.getContext(), fruit.getImg()));
        holder.tvName.setText(fruit.getName());
        holder.tvDesc.setText(fruit.getDesc());
        holder.tvPrice.setText(MoneyUtil.price(fruit.getPrice()));
        holder.tvUnit.setText("/" + fruit.getUnit());
        if (fruit.getStock() > 0) {
            holder.tvStock.setText("库存 " + fruit.getStock());
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.btnAdd.setEnabled(true);
            holder.btnAdd.setAlpha(1f);
        } else {
            holder.tvStock.setText("今日售罄");
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.btnAdd.setEnabled(false);
            holder.btnAdd.setAlpha(0.35f);
        }
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFruitClick(fruit);
            }
        });
        holder.btnAdd.setOnClickListener(v -> {
            if (addListener != null) {
                addListener.onAddCart(fruit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fruitList == null ? 0 : fruitList.size();
    }

    static class FruitHolder extends RecyclerView.ViewHolder {
        final ImageView ivFruit;
        final TextView tvName;
        final TextView tvDesc;
        final TextView tvPrice;
        final TextView tvUnit;
        final TextView tvStock;
        final ImageButton btnAdd;

        FruitHolder(@NonNull View itemView) {
            super(itemView);
            ivFruit = itemView.findViewById(R.id.iv_fruit);
            tvName = itemView.findViewById(R.id.tv_fruit_name);
            tvDesc = itemView.findViewById(R.id.tv_fruit_desc);
            tvPrice = itemView.findViewById(R.id.tv_fruit_price);
            tvUnit = itemView.findViewById(R.id.tv_fruit_unit);
            tvStock = itemView.findViewById(R.id.tv_fruit_stock);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
    }
}
