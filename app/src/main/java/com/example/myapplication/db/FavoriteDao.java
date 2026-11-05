package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.FavoriteItem;
import com.example.myapplication.bean.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏数据访问对象。
 */
public class FavoriteDao {
    private final DBHelper helper;

    public FavoriteDao(Context context) {
        this.helper = new DBHelper(context);
    }

    public void add(Fruit fruit, int shopId) {
        if (fruit == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_FRUIT_ID, fruit.getFruitId());
            values.put(DBHelper.COL_NAME, fruit.getName());
            values.put(DBHelper.COL_PRICE, fruit.getPrice());
            values.put(DBHelper.COL_DESC, fruit.getDesc());
            values.put(DBHelper.COL_IMG, fruit.getImg());
            values.put(DBHelper.COL_STOCK, fruit.getStock());
            values.put(DBHelper.COL_UNIT, fruit.getUnit());
            values.put(DBHelper.COL_SALES, fruit.getSales());
            values.put(DBHelper.COL_SHOP_ID, shopId);
            db.insertWithOnConflict(DBHelper.TABLE_FAVORITE, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public void remove(int fruitId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DBHelper.TABLE_FAVORITE, DBHelper.COL_FRUIT_ID + "=?",
                    new String[]{String.valueOf(fruitId)});
        } finally {
            db.close();
        }
    }

    public boolean isFavorite(int fruitId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_FAVORITE,
                    new String[]{DBHelper.COL_ID},
                    DBHelper.COL_FRUIT_ID + "=?",
                    new String[]{String.valueOf(fruitId)},
                    null, null, null);
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public List<FavoriteItem> queryAll() {
        List<FavoriteItem> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_FAVORITE, null, null, null,
                    null, null, DBHelper.COL_ID + " DESC");
            while (cursor.moveToNext()) {
                FavoriteItem item = new FavoriteItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                item.setFruitId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PRICE)));
                item.setDesc(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DESC)));
                item.setImg(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_IMG)));
                item.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_STOCK)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_UNIT)));
                item.setSales(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_SALES)));
                item.setShopId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_SHOP_ID)));
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

    public int getCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_FAVORITE, null);
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
}
