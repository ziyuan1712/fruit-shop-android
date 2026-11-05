package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Order;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.OrderUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 我的订单列表适配器，支持查看详情与评价订单。
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private final List<Order> orders = new ArrayList<>();
    private final Set<String> reviewedOrderNos = new HashSet<>();
    private final OnOrderClick clickListener;
    private final OnReviewClick reviewListener;

    public interface OnOrderClick {
        void onOrderClick(Order order);
    }

    public interface OnReviewClick {
        void onReviewClick(Order order);
    }

    public OrderAdapter(OnOrderClick clickListener, OnReviewClick reviewListener) {
        this.clickListener = clickListener;
        this.reviewListener = reviewListener;
    }

    public void submit(List<Order> newOrders, Set<String> orderNos) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        reviewedOrderNos.clear();
        if (orderNos != null) {
            reviewedOrderNos.addAll(orderNos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order order = orders.get(position);
        boolean reviewed = reviewedOrderNos.contains(order.getOrderNo());
        holder.tvOrderNo.setText("订单号 " + order.getOrderNo());
        holder.tvStatus.setText(order.getStatus());
        if ("已完成".equals(order.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.brand_green));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.brand_orange_dark));
        }
        holder.tvSummary.setText(OrderUtil.summary(order.getDetail()));
        holder.tvTime.setText(order.getCreateTime());
        String countText = "共 " + order.getItemCount() + " 件";
        if (order.getDiscount() > 0) {
            countText += " · 已优惠 " + MoneyUtil.amount(order.getDiscount());
        }
        holder.tvCount.setText(countText);
        holder.tvTotal.setText(MoneyUtil.amount(order.getTotalPrice()));
        holder.btnAction.setText(reviewed ? "修改评价" : "评价订单");
        holder.btnAction.setEnabled(true);
        holder.btnAction.setAlpha(1f);
        holder.btnAction.setOnClickListener(v -> {
            if (reviewListener != null) {
                reviewListener.onReviewClick(order);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {
        final TextView tvOrderNo;
        final TextView tvStatus;
        final TextView tvSummary;
        final TextView tvTime;
        final TextView tvCount;
        final TextView tvTotal;
        final MaterialButton btnAction;

        OrderHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvSummary = itemView.findViewById(R.id.tv_order_summary);
            tvTime = itemView.findViewById(R.id.tv_order_time);
            tvCount = itemView.findViewById(R.id.tv_order_count);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            btnAction = itemView.findViewById(R.id.btn_order_action);
        }
    }
}
