package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ShopAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.util.JsonUtil;
import com.example.myapplication.util.SPUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 首页：店铺列表、搜索、购物车角标、底部导航。
 * JSON 在子线程解析，并展示加载中 / 加载失败 / 空数据状态。
 */
public class MainActivity extends BaseActivity implements ShopAdapter.OnShopClick {

    private final List<Shop> allShops = new ArrayList<>();
    private final List<Shop> shownShops = new ArrayList<>();
    private ExecutorService executor;
    private ShopAdapter adapter;

    private RecyclerView rvShop;
    private ProgressBar progress;
    private View contentContainer;
    private View emptyView;
    private TextView tvGreeting, tvCartBadge, tvShopCount, tvSearchEmpty;
    private TextView tvEmptyTitle, tvEmptyDesc;
    private MaterialButton btnEmptyAction;
    private EditText etSearch;

    private String currentQuery = "";
    private boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executor = Executors.newSingleThreadExecutor();
        rvShop = findViewById(R.id.rv_shop);
        progress = findViewById(R.id.progress);
        contentContainer = findViewById(R.id.content_container);
        emptyView = findViewById(R.id.empty_view);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        tvShopCount = findViewById(R.id.tv_shop_count);
        tvSearchEmpty = findViewById(R.id.tv_search_empty);
        etSearch = findViewById(R.id.et_search);
        tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);

        adapter = new ShopAdapter(shownShops, this);
        rvShop.setLayoutManager(new LinearLayoutManager(this));
        rvShop.setHasFixedSize(true);
        rvShop.setAdapter(adapter);

        btnEmptyAction.setOnClickListener(v -> loadData());
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();
                if (loaded) {
                    applyFilter();
                }
            }
        });

        findViewById(R.id.btn_header_cart).setOnClickListener(v -> openCart());
        findViewById(R.id.nav_cart).setOnClickListener(v -> openCart());
        findViewById(R.id.nav_profile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            if (adapter.getItemCount() > 0) {
                rvShop.smoothScrollToPosition(0);
            }
        });

        loadData();
    }

    private void loadData() {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        showLoading();
        executor.execute(() -> {
            try {
                final List<Shop> shops = JsonUtil.loadShops(getApplicationContext());
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    onDataLoaded(shops);
                });
            } catch (final Exception exception) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    showLoadError();
                });
            }
        });
    }

    private void onDataLoaded(List<Shop> shops) {
        loaded = true;
        allShops.clear();
        allShops.addAll(shops);
        progress.setVisibility(View.GONE);
        if (allShops.isEmpty()) {
            showEmptyState("暂无店铺", "附近还没有开张的果园", "重新加载");
            return;
        }
        contentContainer.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        applyFilter();
    }

    private void applyFilter() {
        String query = currentQuery.toLowerCase(Locale.CHINA);
        shownShops.clear();
        for (Shop shop : allShops) {
            if (query.isEmpty() || matches(shop, query)) {
                shownShops.add(shop);
            }
        }
        adapter.notifyDataSetChanged();
        tvShopCount.setText("共 " + shownShops.size() + " 家");

        boolean noResult = !query.isEmpty() && shownShops.isEmpty();
        rvShop.setVisibility(noResult ? View.GONE : View.VISIBLE);
        tvSearchEmpty.setVisibility(noResult ? View.VISIBLE : View.GONE);
    }

    private boolean matches(Shop shop, String query) {
        if (contains(shop.getShopName(), query)
                || contains(shop.getShopDesc(), query)
                || contains(shop.getTags(), query)) {
            return true;
        }
        if (shop.getFruitList() == null) {
            return false;
        }
        for (Fruit fruit : shop.getFruitList()) {
            if (contains(fruit.getName(), query) || contains(fruit.getDesc(), query)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(String text, String query) {
        return text != null && text.toLowerCase(Locale.CHINA).contains(query);
    }

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void showLoadError() {
        loaded = true;
        progress.setVisibility(View.GONE);
        showEmptyState("加载失败", "请检查数据文件后重试", "重新加载");
    }

    private void showEmptyState(String title, String desc, String action) {
        contentContainer.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        tvEmptyTitle.setText(title);
        tvEmptyDesc.setText(desc);
        btnEmptyAction.setText(action);
        btnEmptyAction.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreeting();
        updateCartBadge();
    }

    private void updateGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String prefix;
        if (hour < 6) {
            prefix = "夜深了";
        } else if (hour < 11) {
            prefix = "早上好";
        } else if (hour < 14) {
            prefix = "中午好";
        } else if (hour < 18) {
            prefix = "下午好";
        } else {
            prefix = "晚上好";
        }
        tvGreeting.setText(String.format(Locale.CHINA, "%s，%s", prefix, SPUtil.getUsername()));
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
    public void onShopClick(Shop shop) {
        Intent intent = new Intent(this, ShopDetailActivity.class);
        intent.putExtra("shop", shop);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
        super.onDestroy();
    }
}
