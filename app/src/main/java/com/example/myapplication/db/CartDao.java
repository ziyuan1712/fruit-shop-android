package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.CartItem;
import com.example.myapplication.bean.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据访问对象，统一处理 SQLite 增删改查。
 */
public class CartDao {
    private final DBHelper helper;

    public CartDao(Context context) {
        this.helper = new DBHelper(context);
    }

    /**
     * 加入购物车：同一水果已存在时累加数量，否则新增一条记录。
     */
    public void addOrIncrease(Fruit fruit, int shopId, int count) {
        if (fruit == null || count <= 0) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db.query(DBHelper.TABLE_CART,
                    new String[]{DBHelper.COL_ID, DBHelper.COL_COUNT},
                    DBHelper.COL_FRUIT_ID + "=?",
                    new String[]{String.valueOf(fruit.getFruitId())},
                    null, null, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                int oldCount = cursor.getInt(1);
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_COUNT, oldCount + count);
                db.update(DBHelper.TABLE_CART, values,
                        DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_FRUIT_ID, fruit.getFruitId());
                values.put(DBHelper.COL_NAME, fruit.getName());
                values.put(DBHelper.COL_PRICE, fruit.getPrice());
                values.put(DBHelper.COL_IMG, fruit.getImg());
                values.put(DBHelper.COL_COUNT, count);
                values.put(DBHelper.COL_SHOP_ID, shopId);
                values.put(DBHelper.COL_UNIT, fruit.getUnit());
                db.insert(DBHelper.TABLE_CART, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
            db.close();
        }
    }

    public List<CartItem> queryAll() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_CART, null, null, null,
                    null, null, DBHelper.COL_ID + " ASC");
            while (cursor.moveToNext()) {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                item.setFruitId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PRICE)));
                item.setImg(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_IMG)));
                item.setCount(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COUNT)));
                item.setShopId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_SHOP_ID)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_UNIT)));
                list.add(item);
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
     * 购物车内商品种类数：同一种水果无论买几份都只算 1 个。
     */
    public int getKindCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_CART, null);
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

    public void updateCount(int id, int count) {
        if (count <= 0) {
            delete(id);
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_COUNT, count);
            db.update(DBHelper.TABLE_CART, values,
                    DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    public void delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DBHelper.TABLE_CART, DBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    public void clear() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DBHelper.TABLE_CART, null, null);
        } finally {
            db.close();
        }
    }
}
