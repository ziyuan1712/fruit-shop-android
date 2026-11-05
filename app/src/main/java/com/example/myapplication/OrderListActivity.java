package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.bean.CartItem;
import com.example.myapplication.bean.Order;
import com.example.myapplication.bean.Review;
import com.example.myapplication.db.OrderDao;
import com.example.myapplication.db.ReviewDao;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.OrderUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Set;

/**
 * 我的订单：从 SQLite 读取订单，点击可查看商品明细，支持评价订单。
 */
public class OrderListActivity extends BaseActivity
        implements OrderAdapter.OnOrderClick, OrderAdapter.OnReviewClick {

    private OrderAdapter adapter;
    private View emptyView;
    private RecyclerView rvOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvOrder = findViewById(R.id.rv_order);
        emptyView = findViewById(R.id.empty_view);
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setHasFixedSize(true);

        adapter = new OrderAdapter(this, this);
        rvOrder.setAdapter(adapter);

        TextView tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        TextView tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        MaterialButton btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);
        tvEmptyTitle.setText("还没有订单");
        tvEmptyDesc.setText("去挑点新鲜水果，开启第一单吧");
        btnEmptyAction.setText("去逛逛");
        btnEmptyAction.setOnClickListener(v -> goHome());
    }

    private void loadOrders() {
        List<Order> orders = new OrderDao(this).queryAll();
        Set<String> reviewedOrderNos = new ReviewDao(this).queryReviewedOrderNos();
        adapter.submit(orders, reviewedOrderNos);
        boolean empty = orders.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvOrder.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOrderClick(Order order) {
        List<CartItem> items = OrderUtil.parseItems(order.getDetail());
        if (items.isEmpty()) {
            Toast.makeText(this, "订单明细已失效", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (CartItem item : items) {
            builder.append(item.getName())
                    .append(" ×").append(item.getCount())
                    .append("    ")
                    .append(MoneyUtil.amount(item.getSubtotal()))
                    .append('\n');
        }
        builder.append('\n')
                .append("共 ").append(order.getItemCount()).append(" 件商品")
                .append('\n')
                .append("实付：").append(MoneyUtil.amount(order.getTotalPrice()));
        if (order.getDiscount() > 0) {
            builder.append('\n').append("优惠券：-").append(MoneyUtil.amount(order.getDiscount()));
            if (order.getCouponTitle() != null && !order.getCouponTitle().isEmpty()) {
                builder.append("（").append(order.getCouponTitle()).append("）");
            }
        }
        if (order.getNote() != null && !order.getNote().isEmpty()) {
            builder.append('\n').append("备注：").append(order.getNote());
        }
        if (order.getAddress() != null && !order.getAddress().isEmpty()) {
            builder.append('\n').append("配送地址：").append(order.getAddress());
        }
        builder.append('\n').append("状态：").append(order.getStatus());

        Review review = new ReviewDao(this).queryByOrderNo(order.getOrderNo());
        if (review != null) {
            builder.append('\n').append("评价：").append(review.getStars());
            if (review.getContent() != null && !review.getContent().isEmpty()) {
                builder.append('\n').append(review.getContent());
            }
        }

        MaterialAlertDialogBuilder builderDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("订单详情")
                .setMessage(builder.toString())
                .setPositiveButton("知道了", null);
        if (review == null) {
            builderDialog.setNeutralButton("评价订单",
                    (dialog, which) -> DialogUtil.showReviewDialog(this, order.getOrderNo(), this::loadOrders));
        }
        builderDialog.show();
    }

    @Override
    public void onReviewClick(Order order) {
        DialogUtil.showReviewDialog(this, order.getOrderNo(), this::loadOrders);
    }
}
