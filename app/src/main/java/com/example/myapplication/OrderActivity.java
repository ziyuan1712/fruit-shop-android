package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.myapplication.bean.Address;
import com.example.myapplication.bean.CartItem;
import com.example.myapplication.bean.Coupon;
import com.example.myapplication.bean.Order;
import com.example.myapplication.db.AddressDao;
import com.example.myapplication.db.CartDao;
import com.example.myapplication.db.CouponDao;
import com.example.myapplication.db.OrderDao;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.MoneyUtil;
import com.example.myapplication.util.OrderUtil;
import com.example.myapplication.util.ResUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 订单确认页：收货地址、商品清单、备注、金额明细与提交。
 * 提交成功后写入 SQLite 订单表并清空购物车。
 */
public class OrderActivity extends BaseActivity {

    private static final String STATE_COUPON_ID = "selectedCouponId";
    private static final String STATE_ADDRESS_ID = "selectedAddressId";

    private final List<CartItem> cartItems = new ArrayList<>();
    private TextView tvAddressName, tvAddressDetail;
    private TextView tvGoodsAmount, tvDeliveryFee, tvOrderTotal, tvBottomTotal, tvDeliveryTip;
    private TextView tvCouponHint, tvCouponDiscount, tvDiscount;
    private View rowDiscount;
    private LinearLayout llOrderItems;
    private EditText etNote;
    private MaterialButton btnSubmit;
    private Coupon selectedCoupon;
    private Address selectedAddress;
    private AddressDao addressDao;
    private double goodsTotal;
    private double deliveryFee;
    private double discount;
    private double payableTotal;

    private final ActivityResultLauncher<Intent> addressLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                int addressId = result.getData().getIntExtra(AddressActivity.EXTRA_ADDRESS_ID, -1);
                Address address = new AddressDao(this).findById(addressId);
                if (address != null) {
                    selectedAddress = address;
                    renderAddress();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvAddressName = findViewById(R.id.tv_address_name);
        tvAddressDetail = findViewById(R.id.tv_address_detail);
        tvGoodsAmount = findViewById(R.id.tv_goods_amount);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvBottomTotal = findViewById(R.id.tv_bottom_total);
        tvDeliveryTip = findViewById(R.id.tv_delivery_tip);
        tvCouponHint = findViewById(R.id.tv_coupon_hint);
        tvCouponDiscount = findViewById(R.id.tv_coupon_discount);
        tvDiscount = findViewById(R.id.tv_discount);
        rowDiscount = findViewById(R.id.row_discount);
        llOrderItems = findViewById(R.id.ll_order_items);
        etNote = findViewById(R.id.et_note);
        btnSubmit = findViewById(R.id.btn_submit);

        cartItems.addAll(new CartDao(this).queryAll());
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "购物车为空，请先添加商品", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        addressDao = new AddressDao(this);
        selectedAddress = addressDao.getDefault();
        if (savedInstanceState != null) {
            int savedAddressId = savedInstanceState.getInt(STATE_ADDRESS_ID, -1);
            Address savedAddress = addressDao.findById(savedAddressId);
            if (savedAddress != null) {
                selectedAddress = savedAddress;
            }
        }

        renderAddress();
        renderItems();
        calculateTotal();
        restoreCoupon(savedInstanceState);

        ImageView btnEditAddress = findViewById(R.id.btn_edit_address);
        btnEditAddress.setOnClickListener(v -> openAddressSelector());
        findViewById(R.id.card_address).setOnClickListener(v -> openAddressSelector());
        findViewById(R.id.card_coupon).setOnClickListener(v -> chooseCoupon());

        btnSubmit.setOnClickListener(v -> submitOrder());
    }

    private void openAddressSelector() {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra(AddressActivity.EXTRA_SELECT_MODE, true);
        addressLauncher.launch(intent);
    }

    @Override
    protected boolean adjustForIme() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUPON_ID, selectedCoupon == null ? -1 : selectedCoupon.getId());
        outState.putInt(STATE_ADDRESS_ID, selectedAddress == null ? -1 : selectedAddress.getId());
    }

    private void restoreCoupon(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        int couponId = savedInstanceState.getInt(STATE_COUPON_ID, -1);
        if (couponId <= 0) {
            return;
        }
        Coupon coupon = new CouponDao(this).findById(couponId);
        if (coupon != null && !coupon.isUsed()) {
            selectedCoupon = coupon;
            calculateTotal();
        }
    }

    private void renderAddress() {
        if (selectedAddress != null) {
            tvAddressName.setText(selectedAddress.getName() + "  " + maskPhone(selectedAddress.getPhone()));
            tvAddressDetail.setText(selectedAddress.getDetail());
        } else {
            tvAddressName.setText("请选择收货地址");
            tvAddressDetail.setText("点击选择或新增收货地址");
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private void renderItems() {
        llOrderItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartItem item : cartItems) {
            View row = inflater.inflate(R.layout.item_order_product, llOrderItems, false);
            ImageView image = row.findViewById(R.id.iv_order_item_img);
            TextView name = row.findViewById(R.id.tv_order_item_name);
            TextView count = row.findViewById(R.id.tv_order_item_count);
            TextView price = row.findViewById(R.id.tv_order_item_price);
            image.setImageResource(ResUtil.getDrawableId(this, item.getImg()));
            name.setText(item.getName());
            count.setText("×" + item.getCount() + " " + item.getUnit());
            price.setText(MoneyUtil.amount(item.getSubtotal()));
            llOrderItems.addView(row);
        }
    }

    private void chooseCoupon() {
        List<Coupon> available = new CouponDao(this).queryAvailable(OrderUtil.goodsTotal(cartItems));
        DialogUtil.showCouponDialog(this, available, coupon -> {
            selectedCoupon = coupon;
            calculateTotal();
        });
    }

    private void calculateTotal() {
        goodsTotal = OrderUtil.goodsTotal(cartItems);
        deliveryFee = OrderUtil.deliveryFee(goodsTotal);
        discount = selectedCoupon == null ? 0 : Math.min(selectedCoupon.getAmount(), goodsTotal);
        payableTotal = Math.max(0, goodsTotal + deliveryFee - discount);

        tvGoodsAmount.setText(MoneyUtil.amount(goodsTotal));
        tvDeliveryFee.setText(deliveryFee > 0 ? MoneyUtil.amount(deliveryFee) : "免配送费");
        tvOrderTotal.setText(MoneyUtil.amount(payableTotal));
        tvBottomTotal.setText(MoneyUtil.amount(payableTotal));

        if (discount > 0) {
            rowDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + MoneyUtil.amount(discount));
            tvCouponHint.setText("已选：" + selectedCoupon.getTitle());
            tvCouponDiscount.setText("-" + MoneyUtil.amount(discount));
        } else {
            rowDiscount.setVisibility(View.GONE);
            tvCouponHint.setText("选择优惠券");
            tvCouponDiscount.setText("去选择");
        }

        if (deliveryFee > 0) {
            double gap = OrderUtil.FREE_DELIVERY_LIMIT - goodsTotal;
            tvDeliveryTip.setText(String.format(Locale.CHINA, "再买 %s 免配送费", MoneyUtil.amount(gap)));
        } else {
            tvDeliveryTip.setText("已享满 ¥49 免配送费");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (addressDao == null) {
            return;
        }
        if (selectedAddress == null) {
            selectedAddress = addressDao.getDefault();
        } else {
            Address latest = addressDao.findById(selectedAddress.getId());
            selectedAddress = latest != null ? latest : addressDao.getDefault();
        }
        renderAddress();
    }

    private void submitOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "购物车为空，无法提交订单", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedAddress == null) {
            Toast.makeText(this, "请先选择收货地址", Toast.LENGTH_SHORT).show();
            openAddressSelector();
            return;
        }
        btnSubmit.setEnabled(false);

        String orderNo = "GY" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date())
                + (int) (Math.random() * 90 + 10);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
        String note = etNote.getText() == null ? "" : etNote.getText().toString().trim();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setTotalPrice(payableTotal);
        order.setCreateTime(time);
        order.setDetail(OrderUtil.itemsToJson(cartItems));
        order.setStatus("待配送");
        order.setItemCount(OrderUtil.totalCount(cartItems));
        order.setNote(note);
        order.setDiscount(discount);
        order.setCouponTitle(selectedCoupon == null ? "" : selectedCoupon.getTitle());
        order.setAddress(selectedAddress.toOrderAddress());

        long id = new OrderDao(this).insert(order);
        if (id <= 0) {
            btnSubmit.setEnabled(true);
            Toast.makeText(this, "订单提交失败，请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCoupon != null) {
            new CouponDao(this).markUsed(selectedCoupon.getId());
        }
        new CartDao(this).clear();
        showSuccessDialog(orderNo, note);
    }

    private void showSuccessDialog(String orderNo, String note) {
        StringBuilder message = new StringBuilder();
        message.append("订单号：").append(orderNo)
                .append("\n实付金额：").append(MoneyUtil.amount(payableTotal));
        if (discount > 0) {
            message.append("\n优惠券：-").append(MoneyUtil.amount(discount))
                    .append("（").append(selectedCoupon.getTitle()).append("）");
        }
        if (!note.isEmpty()) {
            message.append("\n备注：").append(note);
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("下单成功")
                .setMessage(message.toString())
                .setCancelable(false)
                .setPositiveButton("查看订单", (dialog, which) -> {
                    startActivity(new Intent(this, OrderListActivity.class));
                    finish();
                })
                .setNegativeButton("继续逛逛", (dialog, which) -> goHome())
                .show();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
