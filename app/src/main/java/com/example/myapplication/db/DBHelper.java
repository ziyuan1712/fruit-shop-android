package com.example.myapplication.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库帮助类：
 * v4 新增多收货地址表，并为订单增加收货地址快照字段。
 * v5 扩充优惠券档位；v3 → v4、v4 → v5 均采用增量迁移，保留旧数据。
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fruit_shop.db";
    private static final int VERSION = 5;

    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDER = "orders";
    public static final String TABLE_FAVORITE = "favorites";
    public static final String TABLE_REVIEW = "reviews";
    public static final String TABLE_COUPON = "coupons";
    public static final String TABLE_ADDRESS = "addresses";

    public static final String COL_ID = "id";
    public static final String COL_FRUIT_ID = "fruitId";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_IMG = "img";
    public static final String COL_COUNT = "count";
    public static final String COL_SHOP_ID = "shopId";
    public static final String COL_UNIT = "unit";
    public static final String COL_DESC = "fruitDesc";
    public static final String COL_STOCK = "stock";
    public static final String COL_SALES = "sales";

    public static final String COL_ORDER_NO = "orderNo";
    public static final String COL_TOTAL_PRICE = "totalPrice";
    public static final String COL_CREATE_TIME = "createTime";
    public static final String COL_DETAIL = "detail";
    public static final String COL_STATUS = "status";
    public static final String COL_ITEM_COUNT = "itemCount";
    public static final String COL_NOTE = "note";
    public static final String COL_DISCOUNT = "discount";
    public static final String COL_COUPON_TITLE = "couponTitle";
    public static final String COL_ADDRESS = "address";

    public static final String COL_RATING = "rating";
    public static final String COL_CONTENT = "content";
    public static final String COL_THRESHOLD = "threshold";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_USED = "used";

    public static final String COL_PHONE = "phone";
    public static final String COL_ADDRESS_DETAIL = "addressDetail";
    public static final String COL_IS_DEFAULT = "isDefault";

    public DBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cartSql = "CREATE TABLE " + TABLE_CART + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FRUIT_ID + " INTEGER NOT NULL UNIQUE," +
                COL_NAME + " TEXT," +
                COL_PRICE + " REAL," +
                COL_IMG + " TEXT," +
                COL_COUNT + " INTEGER," +
                COL_SHOP_ID + " INTEGER," +
                COL_UNIT + " TEXT)";
        db.execSQL(cartSql);

        String orderSql = "CREATE TABLE " + TABLE_ORDER + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ORDER_NO + " TEXT," +
                COL_TOTAL_PRICE + " REAL," +
                COL_CREATE_TIME + " TEXT," +
                COL_DETAIL + " TEXT," +
                COL_STATUS + " TEXT," +
                COL_ITEM_COUNT + " INTEGER," +
                COL_NOTE + " TEXT," +
                COL_DISCOUNT + " REAL," +
                COL_COUPON_TITLE + " TEXT," +
                COL_ADDRESS + " TEXT)";
        db.execSQL(orderSql);

        String favoriteSql = "CREATE TABLE " + TABLE_FAVORITE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FRUIT_ID + " INTEGER NOT NULL UNIQUE," +
                COL_NAME + " TEXT," +
                COL_PRICE + " REAL," +
                COL_DESC + " TEXT," +
                COL_IMG + " TEXT," +
                COL_STOCK + " INTEGER," +
                COL_UNIT + " TEXT," +
                COL_SALES + " INTEGER," +
                COL_SHOP_ID + " INTEGER)";
        db.execSQL(favoriteSql);

        String reviewSql = "CREATE TABLE " + TABLE_REVIEW + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ORDER_NO + " TEXT NOT NULL UNIQUE," +
                COL_RATING + " INTEGER," +
                COL_CONTENT + " TEXT," +
                COL_CREATE_TIME + " TEXT)";
        db.execSQL(reviewSql);

        String couponSql = "CREATE TABLE " + TABLE_COUPON + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_THRESHOLD + " REAL," +
                COL_AMOUNT + " REAL," +
                COL_USED + " INTEGER DEFAULT 0)";
        db.execSQL(couponSql);

        db.execSQL(addressTableSql());

        // 预置多档优惠券，下单时按门槛筛选可用券
        seedCoupons(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // v1/v2 结构过旧，直接重建
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUPON);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
            onCreate(db);
            return;
        }
        // v3 → v4：保留原有业务数据，只补地址表与订单地址字段
        if (oldVersion < 4) {
            db.execSQL(addressTableSql());
            addColumnIfMissing(db, TABLE_ORDER, COL_ADDRESS, "TEXT");
        }
        // v4 → v5：补齐新增优惠券档位，不重复、不重置已使用状态
        if (oldVersion < 5) {
            seedCoupons(db);
        }
    }

    /**
     * 按名称补券：已存在的券保持原 used 状态，不会被重置或重复插入。
     */
    private void seedCoupons(SQLiteDatabase db) {
        addCouponIfMissing(db, "新人尝鲜券", 30, 3);
        addCouponIfMissing(db, "满39减4券", 39, 4);
        addCouponIfMissing(db, "满49减5券", 49, 5);
        addCouponIfMissing(db, "满59减6券", 59, 6);
        addCouponIfMissing(db, "满69减8券", 69, 8);
        addCouponIfMissing(db, "满80减10券", 80, 10);
        addCouponIfMissing(db, "满99减15券", 99, 15);
        addCouponIfMissing(db, "满129减20券", 129, 20);
        addCouponIfMissing(db, "满159减25券", 159, 25);
    }

    private void addCouponIfMissing(SQLiteDatabase db, String name, double threshold, double amount) {
        db.execSQL("INSERT INTO " + TABLE_COUPON + "(" + COL_NAME + "," + COL_THRESHOLD + ","
                        + COL_AMOUNT + "," + COL_USED + ") SELECT ?,?,?,0 WHERE NOT EXISTS "
                        + "(SELECT 1 FROM " + TABLE_COUPON + " WHERE " + COL_NAME + "=?)",
                new Object[]{name, threshold, amount, name});
    }

    private String addressTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESS + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_PHONE + " TEXT," +
                COL_ADDRESS_DETAIL + " TEXT," +
                COL_IS_DEFAULT + " INTEGER DEFAULT 0)";
    }

    private void addColumnIfMissing(SQLiteDatabase db, String table, String column, String type) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (column.equals(name)) {
                    return;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
    }
}
