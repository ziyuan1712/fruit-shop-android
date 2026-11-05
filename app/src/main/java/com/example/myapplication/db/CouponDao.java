package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Coupon;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券数据访问对象。
 */
public class CouponDao {
    private final DBHelper helper;

    public CouponDao(Context context) {
        this.helper = new DBHelper(context);
    }

    public List<Coupon> queryAll() {
        List<Coupon> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_COUPON, null, null, null,
                    null, null, DBHelper.COL_USED + " ASC, " + DBHelper.COL_THRESHOLD + " ASC");
            while (cursor.moveToNext()) {
                list.add(readCoupon(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    /**
     * 当前商品金额可用的优惠券，金额越大的优先展示。
     */
    public List<Coupon> queryAvailable(double goodsTotal) {
        List<Coupon> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_COUPON, null,
                    DBHelper.COL_USED + "=0 AND " + DBHelper.COL_THRESHOLD + "<=?",
                    new String[]{String.valueOf(goodsTotal)},
                    null, null, DBHelper.COL_AMOUNT + " DESC");
            while (cursor.moveToNext()) {
                list.add(readCoupon(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    public Coupon findById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_COUPON, null,
                    DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            return cursor.moveToFirst() ? readCoupon(cursor) : null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public int getUnusedCount() {        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_COUPON
                    + " WHERE " + DBHelper.COL_USED + "=0", null);
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

    public void markUsed(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_USED, 1);
            db.update(DBHelper.TABLE_COUPON, values,
                    DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    private Coupon readCoupon(Cursor cursor) {
        Coupon coupon = new Coupon();
        coupon.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
        coupon.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME)));
        coupon.setThreshold(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_THRESHOLD)));
        coupon.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_AMOUNT)));
        coupon.setUsed(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USED)) == 1);
        return coupon;
    }
}
