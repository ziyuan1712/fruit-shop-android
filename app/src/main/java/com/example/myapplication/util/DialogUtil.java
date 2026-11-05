package com.example.myapplication.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;
import com.example.myapplication.bean.Address;
import com.example.myapplication.bean.Coupon;
import com.example.myapplication.bean.Review;
import com.example.myapplication.db.AddressDao;
import com.example.myapplication.db.OrderDao;
import com.example.myapplication.db.ReviewDao;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 通用弹窗：收货地址、订单评价、优惠券选择。
 */
public final class DialogUtil {

    private DialogUtil() {
    }

    public interface OnCouponSelected {
        void onSelected(Coupon coupon);
    }

    public static void showAddressDialog(Context context, Address existing, Runnable onSaved) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_address, null, false);
        TextInputLayout tilName = view.findViewById(R.id.til_address_name);
        TextInputLayout tilPhone = view.findViewById(R.id.til_address_phone);
        TextInputLayout tilDetail = view.findViewById(R.id.til_address_detail);
        TextInputEditText etName = view.findViewById(R.id.et_address_name);
        TextInputEditText etPhone = view.findViewById(R.id.et_address_phone);
        TextInputEditText etDetail = view.findViewById(R.id.et_address_detail);
        MaterialCheckBox cbDefault = view.findViewById(R.id.cb_address_default);

        AddressDao addressDao = new AddressDao(context);
        if (existing != null) {
            etName.setText(existing.getName());
            etPhone.setText(existing.getPhone());
            etDetail.setText(existing.getDetail());
            cbDefault.setChecked(existing.isDefault());
        } else {
            cbDefault.setChecked(addressDao.getCount() == 0);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(existing == null ? "新增收货地址" : "编辑收货地址")
                .setView(view)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    tilName.setError(null);
                    tilPhone.setError(null);
                    tilDetail.setError(null);
                    String name = textOf(etName);
                    String phone = textOf(etPhone);
                    String detail = textOf(etDetail);

                    if (name.isEmpty()) {
                        tilName.setError("请填写收货人姓名");
                        return;
                    }
                    if (!phone.matches("1\\d{10}")) {
                        tilPhone.setError("请填写 11 位手机号");
                        return;
                    }
                    if (detail.length() < 5) {
                        tilDetail.setError("详细地址至少 5 个字");
                        return;
                    }
                    Address address = new Address();
                    if (existing != null) {
                        address.setId(existing.getId());
                    }
                    address.setName(name);
                    address.setPhone(phone);
                    address.setDetail(detail);
                    address.setDefault(cbDefault.isChecked());
                    addressDao.save(address);
                    if (onSaved != null) {
                        onSaved.run();
                    }
                    dialog.dismiss();
                }));
        dialog.show();
    }

    /**
     * 评价订单：保存评价并把订单状态更新为“已完成”。
     */
    public static void showReviewDialog(Context context, String orderNo, Runnable onSaved) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null, false);
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        TextInputEditText etReview = view.findViewById(R.id.et_review);

        ReviewDao reviewDao = new ReviewDao(context);
        Review existing = reviewDao.queryByOrderNo(orderNo);
        if (existing != null) {
            ratingBar.setRating(existing.getRating());
            etReview.setText(existing.getContent());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(existing == null ? "评价订单" : "修改评价")
                .setView(view)
                .setPositiveButton("提交评价", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    int rating = (int) ratingBar.getRating();
                    if (rating <= 0) {
                        Toast.makeText(context, "请先选择星级", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String content = etReview.getText() == null
                            ? "" : etReview.getText().toString().trim();
                    Review review = new Review();
                    review.setOrderNo(orderNo);
                    review.setRating(rating);
                    review.setContent(content);
                    review.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                            .format(new Date()));

                    if (!reviewDao.save(review)) {
                        Toast.makeText(context, "评价保存失败，请重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new OrderDao(context).updateStatus(orderNo, "已完成");
                    Toast.makeText(context, "评价成功，感谢你的分享", Toast.LENGTH_SHORT).show();
                    if (onSaved != null) {
                        onSaved.run();
                    }
                    dialog.dismiss();
                }));
        dialog.show();
    }

    /**
     * 优惠券选择：传入当前商品金额可用的优惠券列表。
     */
    public static void showCouponDialog(Context context, List<Coupon> coupons,
                                        OnCouponSelected callback) {
        if (coupons == null || coupons.isEmpty()) {
            Toast.makeText(context, "当前订单暂无可用优惠券", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] items = new String[coupons.size()];
        for (int i = 0; i < coupons.size(); i++) {
            Coupon coupon = coupons.get(i);
            items[i] = String.format(Locale.CHINA, "%s（满 ¥%s 减 ¥%s）",
                    coupon.getTitle(),
                    MoneyUtil.price(coupon.getThreshold()),
                    MoneyUtil.price(coupon.getAmount()));
        }
        new MaterialAlertDialogBuilder(context)
                .setTitle("选择优惠券")
                .setItems(items, (dialog, which) -> {
                    if (callback != null) {
                        callback.onSelected(coupons.get(which));
                    }
                })
                .setNeutralButton("不使用优惠券", (dialog, which) -> {
                    if (callback != null) {
                        callback.onSelected(null);
                    }
                })
                .show();
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
