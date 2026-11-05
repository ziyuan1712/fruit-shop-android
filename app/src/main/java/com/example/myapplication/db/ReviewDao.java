package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Review;

import java.util.HashSet;
import java.util.Set;

/**
 * 订单评价数据访问对象。
 */
public class ReviewDao {
    private final DBHelper helper;

    public ReviewDao(Context context) {
        this.helper = new DBHelper(context);
    }

    public boolean save(Review review) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_ORDER_NO, review.getOrderNo());
            values.put(DBHelper.COL_RATING, review.getRating());
            values.put(DBHelper.COL_CONTENT, review.getContent());
            values.put(DBHelper.COL_CREATE_TIME, review.getCreateTime());
            long id = db.insertWithOnConflict(DBHelper.TABLE_REVIEW, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            return id > 0;
        } finally {
            db.close();
        }
    }

    public Review queryByOrderNo(String orderNo) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_REVIEW, null,
                    DBHelper.COL_ORDER_NO + "=?",
                    new String[]{orderNo}, null, null, null);
            if (!cursor.moveToFirst()) {
                return null;
            }
            Review review = new Review();
            review.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
            review.setOrderNo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_NO)));
            review.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_RATING)));
            review.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CONTENT)));
            review.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CREATE_TIME)));
            return review;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public Set<String> queryReviewedOrderNos() {
        Set<String> result = new HashSet<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_REVIEW,
                    new String[]{DBHelper.COL_ORDER_NO},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return result;
    }
}
