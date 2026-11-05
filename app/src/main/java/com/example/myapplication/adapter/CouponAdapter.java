package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Coupon;
import com.example.myapplication.util.MoneyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 我的优惠券列表适配器。
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponHolder> {
    private final List<Coupon> items = new ArrayList<>();

    public void setData(List<Coupon> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coupon, parent, false);
        return new CouponHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponHolder holder, int position) {
        Coupon coupon = items.get(position);
        holder.tvAmount.setText(MoneyUtil.price(coupon.getAmount()));
        holder.tvThreshold.setText(String.format(Locale.CHINA, "满 ¥%s 可用",
                MoneyUtil.price(coupon.getThreshold())));
        holder.tvTitle.setText(coupon.getTitle());
        holder.tvDesc.setText(coupon.isUsed() ? "已在订单中使用" : "下单时按门槛自动可选");
        holder.tvStatus.setText(coupon.isUsed() ? "已使用" : "待使用");
        holder.itemView.setAlpha(coupon.isUsed() ? 0.55f : 1f);
        if (coupon.isUsed()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_stepper);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.text_hint));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.brand_green));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CouponHolder extends RecyclerView.ViewHolder {
        final TextView tvAmount;
        final TextView tvThreshold;
        final TextView tvTitle;
        final TextView tvDesc;
        final TextView tvStatus;

        CouponHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_coupon_amount);
            tvThreshold = itemView.findViewById(R.id.tv_coupon_threshold);
            tvTitle = itemView.findViewById(R.id.tv_coupon_title);
            tvDesc = itemView.findViewById(R.id.tv_coupon_desc);
            tvStatus = itemView.findViewById(R.id.tv_coupon_status);
        }
    }
}
