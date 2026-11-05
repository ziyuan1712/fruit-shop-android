package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.AddressAdapter;
import com.example.myapplication.bean.Address;
import com.example.myapplication.db.AddressDao;
import com.example.myapplication.util.DialogUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * 收货地址管理：支持多地址、新增、编辑、删除、设为默认；
 * 从确认订单页进入时作为地址选择页使用。
 */
public class AddressActivity extends BaseActivity implements AddressAdapter.Listener {

    public static final String EXTRA_SELECT_MODE = "select_mode";
    public static final String EXTRA_ADDRESS_ID = "address_id";

    private AddressAdapter adapter;
    private View emptyView;
    private RecyclerView rvAddress;
    private boolean selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        selectMode = getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(selectMode ? "选择收货地址" : "收货地址");

        rvAddress = findViewById(R.id.rv_address);
        emptyView = findViewById(R.id.empty_view);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
        rvAddress.setHasFixedSize(true);
        adapter = new AddressAdapter(this);
        rvAddress.setAdapter(adapter);

        ImageView ivEmpty = emptyView.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.drawable.ic_location);
        TextView tvEmptyTitle = emptyView.findViewById(R.id.tv_empty_title);
        TextView tvEmptyDesc = emptyView.findViewById(R.id.tv_empty_desc);
        MaterialButton btnEmptyAction = emptyView.findViewById(R.id.btn_empty_action);
        tvEmptyTitle.setText("还没有收货地址");
        tvEmptyDesc.setText("添加地址后，下单时可以直接选用");
        btnEmptyAction.setText("新增地址");
        btnEmptyAction.setOnClickListener(v -> addAddress());

        findViewById(R.id.btn_add_address).setOnClickListener(v -> addAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        List<Address> addresses = new AddressDao(this).queryAll();
        adapter.setData(addresses);
        boolean empty = addresses.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvAddress.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void addAddress() {
        DialogUtil.showAddressDialog(this, null, this::loadAddresses);
    }

    @Override
    public void onAddressClick(Address address) {
        if (selectMode) {
            Intent data = new Intent();
            data.putExtra(EXTRA_ADDRESS_ID, address.getId());
            setResult(RESULT_OK, data);
            finish();
        } else {
            editAddress(address);
        }
    }

    @Override
    public void onEdit(Address address) {
        editAddress(address);
    }

    private void editAddress(Address address) {
        DialogUtil.showAddressDialog(this, address, this::loadAddresses);
    }

    @Override
    public void onDelete(Address address) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("删除地址")
                .setMessage("确定删除该收货地址吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (dialog, which) -> {
                    new AddressDao(this).delete(address.getId());
                    loadAddresses();
                    Toast.makeText(this, "地址已删除", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onSetDefault(Address address) {
        new AddressDao(this).setDefault(address.getId());
        loadAddresses();
        Toast.makeText(this, "已设为默认地址", Toast.LENGTH_SHORT).show();
    }
}
