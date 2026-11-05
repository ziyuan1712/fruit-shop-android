package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.FavoriteAdapter;
import com.example.myapplication.bean.FavoriteItem;
import com.example.myapplication.db.FavoriteDao;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * 我的收藏：展示收藏的水果，可进入详情或直接取消收藏。
 */
public class FavoritesActivity extends BaseActivity implements FavoriteAdapter.Listener {

    private FavoriteAdapter adapter;
    private View emptyView;
    private RecyclerView rvFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvFavorite = findViewById(R.id.rv_favorite);
        emptyView = findViewById(R.id.empty_view);
        rvFavorite.setLayoutManager(new LinearLayoutManager(this));
        rvFavorite.setHasFixedSize(true);
        adapter = new FavoriteAdapter(this);
        rvFavorite.setAdapter(adapter);

        ImageView ivEmpty = emptyView.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.drawable.ic_favorite_border);
        TextView tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        TextView tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        MaterialButton btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);
        tvEmptyTitle.setText("还没有收藏");
        tvEmptyDesc.setText("在水果详情页点亮爱心，就能在这里快速找到它");
        btnEmptyAction.setText("去逛逛");
        btnEmptyAction.setOnClickListener(v -> goHome());
    }

    private void loadFavorites() {
        List<FavoriteItem> items = new FavoriteDao(this).queryAll();
        adapter.setData(items);
        boolean empty = items.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvFavorite.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public void onFavoriteClick(FavoriteItem item) {
        Intent intent = new Intent(this, FruitDetailActivity.class);
        intent.putExtra("fruit", item.toFruit());
        intent.putExtra("shopId", item.getShopId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteRemove(FavoriteItem item) {
        new FavoriteDao(this).remove(item.getFruitId());
        loadFavorites();
        Toast.makeText(this, "已取消收藏：" + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
