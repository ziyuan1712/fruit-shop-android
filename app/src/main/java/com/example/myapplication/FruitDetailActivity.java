package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.bean.Fruit;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.db.FavoriteDao;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.ResUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

/**
 * 水果详情页：大图、名称、价格、描述、库存与数量选择，支持加入购物车。
 */
public class FruitDetailActivity extends BaseActivity {

    private Fruit fruit;
    private int shopId;
    private int quantity = 1;
    private int maxStock = 1;
    private TextView tvQty, tvCartBadge;
    private ImageView btnMinus, btnPlus;
    private MaterialButton btnAddCart;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        try {
            fruit = (Fruit) getIntent().getSerializableExtra("fruit");
        } catch (ClassCastException exception) {
            fruit = null;
        }
        shopId = getIntent().getIntExtra("shopId", 1);
        if (fruit == null) {
            Toast.makeText(this, "商品信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar.inflateMenu(R.menu.menu_fruit_detail);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        updateFavoriteMenu();

        tvQty = findViewById(R.id.tv_qty);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        btnAddCart = findViewById(R.id.btn_add_cart);

        ImageView ivFruit = findViewById(R.id.iv_fruit_detail);
        TextView tvName = findViewById(R.id.tv_fruit_name);
        TextView tvPrice = findViewById(R.id.tv_fruit_price);
        TextView tvUnit = findViewById(R.id.tv_fruit_unit);
        TextView tvDesc = findViewById(R.id.tv_fruit_desc);
        TextView tvStock = findViewById(R.id.tv_fruit_stock);
        TextView tvSales = findViewById(R.id.tv_fruit_sales);
        TextView tvDetail = findViewById(R.id.tv_detail);

        ivFruit.setImageResource(ResUtil.getDrawableId(this, fruit.getImg()));
        tvName.setText(fruit.getName());
        tvPrice.setText(MoneyUtil.price(fruit.getPrice()));
        tvUnit.setText("/" + fruit.getUnit());
        tvDesc.setText(fruit.getDesc());
        tvStock.setText("库存 " + fruit.getStock() + " " + fruit.getUnit());
        tvSales.setText("已售 " + fruit.getSales());
        tvDetail.setText(buildDetailText());

        maxStock = Math.max(fruit.getStock(), 1);
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                renderQuantity();
            }
        });
        btnPlus.setOnClickListener(v -> {
            if (quantity < maxStock && quantity < fruit.getStock()) {
                quantity++;
                renderQuantity();
            } else {
                Toast.makeText(this, "已达到库存上限", Toast.LENGTH_SHORT).show();
            }
        });
        btnAddCart.setOnClickListener(v -> addToCart());
        findViewById(R.id.btn_cart).setOnClickListener(v -> openCart());

        if (fruit.getStock() <= 0) {
            btnAddCart.setEnabled(false);
            btnAddCart.setAlpha(0.45f);
            btnAddCart.setText("已售罄");
            btnPlus.setEnabled(false);
            btnMinus.setEnabled(false);
        }
        renderQuantity();
    }

    private String buildDetailText() {
        return fruit.getDesc()
                + "\n\n产地：合作果园直供"
                + "\n储存：建议冷藏保存，尽快食用"
                + "\n售后：坏果包赔，签收后 24 小时内可联系客服";
    }

    private void renderQuantity() {
        tvQty.setText(String.valueOf(quantity));
        btnMinus.setEnabled(quantity > 1);
        btnMinus.setAlpha(quantity > 1 ? 1f : 0.3f);
        btnPlus.setEnabled(quantity < maxStock && quantity < fruit.getStock());
        btnPlus.setAlpha(quantity < maxStock && quantity < fruit.getStock() ? 1f : 0.3f);
    }

    private void addToCart() {
        if (fruit.getStock() <= 0) {
            Toast.makeText(this, "该商品已售罄", Toast.LENGTH_SHORT).show();
            return;
        }
        new CartDao(this).addOrIncrease(fruit, shopId, quantity);
        Toast.makeText(this,
                String.format(Locale.CHINA, "已加入购物车：%s ×%d", fruit.getName(), quantity),
                Toast.LENGTH_SHORT).show();
        updateCartBadge();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() != R.id.action_favorite) {
            return false;
        }
        FavoriteDao dao = new FavoriteDao(this);
        boolean favorite = dao.isFavorite(fruit.getFruitId());
        if (favorite) {
            dao.remove(fruit.getFruitId());
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        } else {
            dao.add(fruit, shopId);
            Toast.makeText(this, "已加入我的收藏", Toast.LENGTH_SHORT).show();
        }
        updateFavoriteMenu();
        return true;
    }

    private void updateFavoriteMenu() {
        if (toolbar == null || fruit == null) {
            return;
        }
        MenuItem item = toolbar.getMenu().findItem(R.id.action_favorite);
        if (item == null) {
            return;
        }
        boolean favorite = new FavoriteDao(this).isFavorite(fruit.getFruitId());
        item.setIcon(favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        item.setTitle(favorite ? "已收藏" : "收藏");
    }

    private void openCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fruit == null || tvCartBadge == null) {
            return;
        }
        updateCartBadge();
        updateFavoriteMenu();
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
}
