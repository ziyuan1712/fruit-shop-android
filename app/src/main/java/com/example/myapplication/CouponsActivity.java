package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.CouponAdapter;
import com.example.myapplication.bean.Coupon;
import com.example.myapplication.db.CouponDao;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * 我的优惠券：展示可用与已使用的优惠券。
 */
public class CouponsActivity extends BaseActivity {

    private CouponAdapter adapter;
    private View emptyView;
    private RecyclerView rvCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvCoupon = findViewById(R.id.rv_coupon);
        emptyView = findViewById(R.id.empty_view);
        rvCoupon.setLayoutManager(new LinearLayoutManager(this));
        rvCoupon.setHasFixedSize(true);
        adapter = new CouponAdapter();
        rvCoupon.setAdapter(adapter);

        ImageView ivEmpty = emptyView.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.drawable.ic_coupon);
        TextView tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        TextView tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        MaterialButton btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);
        tvEmptyTitle.setText("暂无优惠券");
        tvEmptyDesc.setText("下单满足门槛后即可使用优惠券");
        btnEmptyAction.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCoupons();
    }

    private void loadCoupons() {
        List<Coupon> coupons = new CouponDao(this).queryAll();
        adapter.setData(coupons);
        boolean empty = coupons.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvCoupon.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
