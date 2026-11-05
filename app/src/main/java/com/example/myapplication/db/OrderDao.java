package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问对象。
 */
public class OrderDao {
    private final DBHelper helper;

    public OrderDao(Context context) {
        this.helper = new DBHelper(context);
    }

    public long insert(Order order) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_ORDER_NO, order.getOrderNo());
            values.put(DBHelper.COL_TOTAL_PRICE, order.getTotalPrice());
            values.put(DBHelper.COL_CREATE_TIME, order.getCreateTime());
            values.put(DBHelper.COL_DETAIL, order.getDetail());
            values.put(DBHelper.COL_STATUS, order.getStatus());
            values.put(DBHelper.COL_ITEM_COUNT, order.getItemCount());
            values.put(DBHelper.COL_NOTE, order.getNote());
            values.put(DBHelper.COL_DISCOUNT, order.getDiscount());
            values.put(DBHelper.COL_COUPON_TITLE, order.getCouponTitle());
            values.put(DBHelper.COL_ADDRESS, order.getAddress());
            long id = db.insert(DBHelper.TABLE_ORDER, null, values);
            order.setId(id);
            return id;
        } finally {
            db.close();
        }
    }

    public List<Order> queryAll() {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_ORDER, null, null, null,
                    null, null, DBHelper.COL_ID + " DESC");
            while (cursor.moveToNext()) {
                Order order = new Order();
                order.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                order.setOrderNo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_NO)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_TOTAL_PRICE)));
                order.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CREATE_TIME)));
                order.setDetail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DETAIL)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_STATUS)));
                order.setItemCount(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ITEM_COUNT)));
                order.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOTE)));
                order.setDiscount(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_DISCOUNT)));
                order.setCouponTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COUPON_TITLE)));
                order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ADDRESS)));
                list.add(order);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    public int getCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_ORDER, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void updateStatus(String orderNo, String status) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_STATUS, status);
            db.update(DBHelper.TABLE_ORDER, values,
                    DBHelper.COL_ORDER_NO + "=?", new String[]{orderNo});
        } finally {
            db.close();
        }
    }
}
