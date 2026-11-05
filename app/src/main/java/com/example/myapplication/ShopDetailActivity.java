package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.OrderUtil;
import com.example.myapplication.util.ResUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

/**
 * 店铺详情页：展示店铺信息与在售水果列表，支持一键加购。
 */
public class ShopDetailActivity extends BaseActivity
        implements FruitAdapter.OnFruitClick, FruitAdapter.OnAddCart {

    private Shop shop;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        try {
            shop = (Shop) getIntent().getSerializableExtra("shop");
        } catch (ClassCastException exception) {
            shop = null;
        }
        if (shop == null) {
            Toast.makeText(this, "店铺信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCartBadge = findViewById(R.id.tv_cart_badge);
        ImageView ivCover = findViewById(R.id.iv_shop_cover);
        TextView tvName = findViewById(R.id.tv_shop_name);
        TextView tvDesc = findViewById(R.id.tv_shop_desc);
        TextView tvRating = findViewById(R.id.tv_shop_rating);
        TextView tvDelivery = findViewById(R.id.tv_shop_delivery);
        TextView tvDistance = findViewById(R.id.tv_shop_distance);
        TextView tvTags = findViewById(R.id.tv_shop_tags);
        TextView tvNotice = findViewById(R.id.tv_shop_notice);
        TextView tvFruitCount = findViewById(R.id.tv_fruit_count);
        RecyclerView rvFruit = findViewById(R.id.rv_fruit);

        ivCover.setImageResource(ResUtil.getDrawableId(this, shop.getShopImg()));
        tvName.setText(shop.getShopName());
        tvDesc.setText(shop.getShopDesc());
        tvRating.setText(String.format(Locale.CHINA, "★ %.1f", shop.getRating()));
        String delivery = shop.getDeliveryTime() == null ? "" : shop.getDeliveryTime();
        tvDelivery.setText(delivery.endsWith("达") ? delivery : delivery + "达");
        tvDistance.setText(shop.getDistance());
        String tags = shop.getTags() == null ? "" : shop.getTags().replace(",", "  ·  ");
        tvTags.setText(tags.isEmpty() ? "产地直采  ·  新鲜配送" : tags);
        tvNotice.setText(String.format(Locale.CHINA, "起送 ¥%s  ·  满 ¥%s 免配送费",
                MoneyUtil.price(shop.getMinOrder()), MoneyUtil.price(OrderUtil.FREE_DELIVERY_LIMIT)));

        List<Fruit> fruits = shop.getFruitList();
        tvFruitCount.setText("共 " + (fruits == null ? 0 : fruits.size()) + " 种");

        rvFruit.setLayoutManager(new GridLayoutManager(this, 2));
        rvFruit.setNestedScrollingEnabled(false);
        rvFruit.setAdapter(new FruitAdapter(fruits, this, this));

        findViewById(R.id.btn_cart).setOnClickListener(v -> openCart());
        MaterialButton btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> openCart());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shop == null || tvCartBadge == null) {
            return;
        }
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = new CartDao(this).getKindCount();
        if (count <= 0) {
            tvCartBadge.setVisibility(View.GONE);
        } else {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        }
    }

    private void openCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    @Override
    public void onFruitClick(Fruit fruit) {
        Intent intent = new Intent(this, FruitDetailActivity.class);
        intent.putExtra("fruit", fruit);
        intent.putExtra("shopId", shop.getShopId());
        startActivity(intent);
    }

    @Override
    public void onAddCart(Fruit fruit) {
        if (fruit.getStock() <= 0) {
            Toast.makeText(this, "该商品已售罄", Toast.LENGTH_SHORT).show();
            return;
        }
        new CartDao(this).addOrIncrease(fruit, shop.getShopId(), 1);
        Toast.makeText(this, "已加入购物车：" + fruit.getName(), Toast.LENGTH_SHORT).show();
        updateCartBadge();
    }
}
