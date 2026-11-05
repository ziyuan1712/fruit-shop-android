package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.bean.CartItem;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.util.MoneyUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车页面：RecyclerView 展示、数量增减、删除、清空、去结算。
 */
public class CartActivity extends BaseActivity implements CartAdapter.Listener {

    private RecyclerView rvCart;
    private View emptyView;
    private TextView tvTotalPrice;
    private MaterialButton btnSubmitOrder;
    private CartAdapter adapter;
    private final List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        rvCart = findViewById(R.id.rv_cart);
        emptyView = findViewById(R.id.empty_view);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setHasFixedSize(true);
        adapter = new CartAdapter(this);
        rvCart.setAdapter(adapter);

        TextView tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        TextView tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        MaterialButton btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);
        tvEmptyTitle.setText("购物车还是空的");
        tvEmptyDesc.setText("去挑几样新鲜水果吧");
        btnEmptyAction.setText("去逛逛");
        btnEmptyAction.setOnClickListener(v -> goHome());

        btnSubmitOrder.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "请先添加商品", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, OrderActivity.class));
        });
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            confirmClear();
            return true;
        }
        return false;
    }

    private void loadCartData() {
        cartItems.clear();
        cartItems.addAll(new CartDao(this).queryAll());
        adapter.setData(cartItems);
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        tvTotalPrice.setText(MoneyUtil.amount(total));
        boolean empty = cartItems.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        btnSubmitOrder.setEnabled(!empty);
        btnSubmitOrder.setAlpha(empty ? 0.45f : 1f);
    }

    private void confirmClear() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "购物车已经是空的了", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("清空购物车")
                .setMessage("确定要删除购物车中的全部商品吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("清空", (dialog, which) -> {
                    new CartDao(this).clear();
                    loadCartData();
                    Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onMinus(CartItem item) {
        if (item.getCount() <= 1) {
            return;
        }
        new CartDao(this).updateCount(item.getId(), item.getCount() - 1);
        loadCartData();
    }

    @Override
    public void onPlus(CartItem item) {
        new CartDao(this).updateCount(item.getId(), item.getCount() + 1);
        loadCartData();
    }

    @Override
    public void onDelete(CartItem item) {
        new CartDao(this).delete(item.getId());
        loadCartData();
        Toast.makeText(this, "已删除 " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartData();
    }
}
