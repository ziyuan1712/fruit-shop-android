package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.bean.Address;
import com.example.myapplication.db.AddressDao;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.db.CouponDao;
import com.example.myapplication.db.FavoriteDao;
import com.example.myapplication.db.OrderDao;
import com.example.myapplication.util.SPUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * 个人中心：用户信息、订单 / 购物车统计、收货地址、关于与退出登录。
 */
public class ProfileActivity extends BaseActivity {

    private TextView tvAvatar, tvUsername;
    private TextView tvStatOrders, tvStatCart, tvStatAddress;
    private TextView tvOrdersHint, tvCartHint, tvAddressHint;
    private TextView tvFavoritesHint, tvCouponsHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvAvatar = findViewById(R.id.tv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvStatOrders = findViewById(R.id.tv_stat_orders);
        tvStatCart = findViewById(R.id.tv_stat_cart);
        tvStatAddress = findViewById(R.id.tv_stat_address);
        tvOrdersHint = findViewById(R.id.tv_orders_hint);
        tvCartHint = findViewById(R.id.tv_cart_hint);
        tvAddressHint = findViewById(R.id.tv_address_hint);
        tvFavoritesHint = findViewById(R.id.tv_favorites_hint);
        tvCouponsHint = findViewById(R.id.tv_coupons_hint);

        findViewById(R.id.row_orders).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.stat_orders).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.row_favorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.row_cart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.stat_cart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.row_coupons).setOnClickListener(v ->
                startActivity(new Intent(this, CouponsActivity.class)));
        findViewById(R.id.row_address).setOnClickListener(v -> openAddresses());
        findViewById(R.id.stat_address).setOnClickListener(v -> openAddresses());
        findViewById(R.id.row_about).setOnClickListener(v -> showAbout());
        findViewById(R.id.btn_logout).setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        String username = SPUtil.getUsername();
        tvUsername.setText(username);
        tvAvatar.setText(username.isEmpty() ? "果" : username.substring(0, 1).toUpperCase(Locale.CHINA));

        int orderCount = new OrderDao(this).getCount();
        int cartCount = new CartDao(this).getKindCount();
        int favoriteCount = new FavoriteDao(this).getCount();
        int couponCount = new CouponDao(this).getUnusedCount();
        AddressDao addressDao = new AddressDao(this);
        int addressCount = addressDao.getCount();
        Address defaultAddress = addressDao.getDefault();
        tvStatOrders.setText(String.valueOf(orderCount));
        tvStatCart.setText(String.valueOf(cartCount));
        tvStatAddress.setText(addressCount > 0 ? "已设置" : "未设置");
        tvOrdersHint.setText(orderCount > 0 ? orderCount + " 笔" : "");
        tvCartHint.setText(cartCount > 0 ? cartCount + " 种" : "");
        tvFavoritesHint.setText(favoriteCount > 0 ? favoriteCount + " 件" : "");
        tvCouponsHint.setText(couponCount > 0 ? couponCount + " 张" : "");
        tvAddressHint.setText(defaultAddress != null ? defaultAddress.getName() : "去设置");
    }

    private void openAddresses() {
        startActivity(new Intent(this, AddressActivity.class));
    }

    private void showAbout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("关于果悦鲜生")
                .setMessage("果悦鲜生 v2.1\n\n校园里的新鲜水果商店，精选产地直采鲜果，30 分钟送达。\n\n"
                        + "本项目为 Android 课程综合实践作品，数据均为演示数据。")
                .setPositiveButton("好的", null)
                .show();
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", (dialog, which) -> {
                    SPUtil.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
