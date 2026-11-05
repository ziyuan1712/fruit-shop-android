package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具：账号、登录状态、收货地址。
 */
public final class SPUtil {
    private static final String SP_NAME = "guoyue_user";
    private static final String KEY_IS_LOGIN = "isLogin";
    private static final String KEY_CURRENT_USER = "currentUser";
    private static final String KEY_LAST_USER = "lastUser";
    private static final String KEY_SEEDED = "demoSeeded";
    private static final String KEY_ADDRESS_NAME = "addressName";
    private static final String KEY_ADDRESS_PHONE = "addressPhone";
    private static final String KEY_ADDRESS_DETAIL = "addressDetail";
    private static final String KEY_ADDRESS_MIGRATED = "addressMigrated";
    private static final String DEMO_USER = "admin";
    private static final String DEMO_PASSWORD = "123456";

    private static SharedPreferences sp;

    private SPUtil() {
    }

    public static void init(Context context) {
        if (sp == null) {
            sp = context.getApplicationContext()
                    .getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            seedDemoAccount();
        }
    }

    private static void seedDemoAccount() {
        if (!sp.getBoolean(KEY_SEEDED, false)) {
            sp.edit()
                    .putString(accountKey(DEMO_USER), DEMO_PASSWORD)
                    .putBoolean(KEY_SEEDED, true)
                    .apply();
        }
    }

    private static String accountKey(String username) {
        return "account_" + username;
    }

    public static boolean hasAccount(String username) {
        return sp != null && sp.contains(accountKey(username));
    }

    /**
     * 注册新账号，用户名已存在时返回 false。
     */
    public static boolean register(String username, String password) {
        if (sp == null || username == null || password == null || hasAccount(username)) {
            return false;
        }
        sp.edit()
                .putString(accountKey(username), password)
                .putString(KEY_LAST_USER, username)
                .apply();
        return true;
    }

    public static boolean checkPassword(String username, String password) {
        return sp != null && password != null
                && password.equals(sp.getString(accountKey(username), ""));
    }

    /**
     * 登录成功后保存状态；remember 为 false 时不写入自动登录标记。
     */
    public static void saveSession(String username, boolean remember) {
        SharedPreferences.Editor editor = sp.edit()
                .putString(KEY_CURRENT_USER, username)
                .putString(KEY_LAST_USER, username);
        if (remember) {
            editor.putBoolean(KEY_IS_LOGIN, true);
        } else {
            editor.remove(KEY_IS_LOGIN);
        }
        editor.apply();
    }

    public static boolean isLoggedIn() {
        return sp != null && sp.getBoolean(KEY_IS_LOGIN, false);
    }

    public static String getUsername() {
        if (sp == null) {
            return "果友";
        }
        String name = sp.getString(KEY_CURRENT_USER, "");
        if (name.isEmpty()) {
            name = sp.getString(KEY_LAST_USER, "");
        }
        return name.isEmpty() ? "果友" : name;
    }

    public static String getLastUsername() {
        return sp == null ? "" : sp.getString(KEY_LAST_USER, "");
    }

    public static void logout() {
        if (sp == null) {
            return;
        }
        sp.edit()
                .remove(KEY_IS_LOGIN)
                .remove(KEY_CURRENT_USER)
                .apply();
    }

    public static String getAddressName() {
        return sp == null ? "" : sp.getString(KEY_ADDRESS_NAME, "");
    }

    public static String getAddressPhone() {
        return sp == null ? "" : sp.getString(KEY_ADDRESS_PHONE, "");
    }

    public static String getAddressDetail() {
        return sp == null ? "" : sp.getString(KEY_ADDRESS_DETAIL, "");
    }

    public static boolean hasAddress() {
        return !getAddressName().isEmpty()
                && !getAddressPhone().isEmpty()
                && !getAddressDetail().isEmpty();
    }

    public static boolean isAddressMigrated() {
        return sp != null && sp.getBoolean(KEY_ADDRESS_MIGRATED, false);
    }

    public static void markAddressMigrated() {
        if (sp != null) {
            sp.edit().putBoolean(KEY_ADDRESS_MIGRATED, true).apply();
        }
    }
}
