package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Address;
import com.example.myapplication.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 收货地址数据访问对象：支持多地址、默认地址与旧版单地址迁移。
 */
public class AddressDao {
    private final DBHelper helper;

    public AddressDao(Context context) {
        this.helper = new DBHelper(context);
        migrateLegacyAddress();
    }

    /**
     * 旧版本把地址保存在 SharedPreferences 中，首次升级后迁移为一条默认地址。
     * 使用标记避免用户删光地址后被反复迁移。
     */
    private void migrateLegacyAddress() {
        if (SPUtil.isAddressMigrated()) {
            return;
        }
        SPUtil.markAddressMigrated();
        if (!SPUtil.hasAddress() || getCount() > 0) {
            return;
        }
        Address address = new Address();
        address.setName(SPUtil.getAddressName());
        address.setPhone(SPUtil.getAddressPhone());
        address.setDetail(SPUtil.getAddressDetail());
        address.setDefault(true);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_NAME, address.getName());
            values.put(DBHelper.COL_PHONE, address.getPhone());
            values.put(DBHelper.COL_ADDRESS_DETAIL, address.getDetail());
            values.put(DBHelper.COL_IS_DEFAULT, 1);
            db.insert(DBHelper.TABLE_ADDRESS, null, values);
        } finally {
            db.close();
        }
    }

    public List<Address> queryAll() {
        List<Address> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_ADDRESS, null, null, null,
                    null, null, DBHelper.COL_IS_DEFAULT + " DESC, " + DBHelper.COL_ID + " DESC");
            while (cursor.moveToNext()) {
                list.add(readAddress(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    public Address getDefault() {
        Address address = queryFirst(DBHelper.COL_IS_DEFAULT + "=1");
        if (address == null) {
            address = queryFirst(null);
        }
        return address;
    }

    public Address findById(int id) {
        return queryFirst(DBHelper.COL_ID + "=" + id);
    }

    public int getCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_ADDRESS, null);
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * 新增或更新地址，并维护“仅一个默认地址”的约束。
     */
    public void save(Address address) {
        boolean wasDefault = false;
        if (address.getId() > 0) {
            Address old = findById(address.getId());
            wasDefault = old != null && old.isDefault();
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_NAME, address.getName());
            values.put(DBHelper.COL_PHONE, address.getPhone());
            values.put(DBHelper.COL_ADDRESS_DETAIL, address.getDetail());
            values.put(DBHelper.COL_IS_DEFAULT, address.isDefault() ? 1 : 0);
            if (address.getId() > 0) {
                db.update(DBHelper.TABLE_ADDRESS, values,
                        DBHelper.COL_ID + "=?", new String[]{String.valueOf(address.getId())});
            } else {
                long id = db.insert(DBHelper.TABLE_ADDRESS, null, values);
                address.setId((int) id);
            }
        } finally {
            db.close();
        }

        if (address.isDefault()) {
            setDefault(address.getId());
        } else if (wasDefault) {
            ensureDefaultExcluding(address.getId());
        } else if (getDefault() == null) {
            setDefault(address.getId());
        }
    }

    public void setDefault(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues clear = new ContentValues();
            clear.put(DBHelper.COL_IS_DEFAULT, 0);
            db.update(DBHelper.TABLE_ADDRESS, clear, null, null);
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_IS_DEFAULT, 1);
            db.update(DBHelper.TABLE_ADDRESS, values,
                    DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void delete(int id) {
        Address address = findById(id);
        if (address == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DBHelper.TABLE_ADDRESS, DBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
        if (address.isDefault()) {
            ensureDefaultExcluding(id);
        }
    }

    private void ensureDefaultExcluding(int excludeId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        int nextId = -1;
        try {
            cursor = db.query(DBHelper.TABLE_ADDRESS,
                    new String[]{DBHelper.COL_ID},
                    DBHelper.COL_ID + "<>?", new String[]{String.valueOf(excludeId)},
                    null, null, DBHelper.COL_ID + " ASC", "1");
            if (cursor.moveToFirst()) {
                nextId = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        if (nextId > 0) {
            setDefault(nextId);
        } else {
            // 只剩当前地址时仍保持默认，避免出现“有地址但无默认”
            setDefault(excludeId);
        }
    }

    private Address queryFirst(String selection) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_ADDRESS, null, selection, null,
                    null, null, DBHelper.COL_ID + " ASC", "1");
            if (cursor.moveToFirst()) {
                return readAddress(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private Address readAddress(Cursor cursor) {
        Address address = new Address();
        address.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
        address.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME)));
        address.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PHONE)));
        address.setDetail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ADDRESS_DETAIL)));
        address.setDefault(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_IS_DEFAULT)) == 1);
        return address;
    }
}
