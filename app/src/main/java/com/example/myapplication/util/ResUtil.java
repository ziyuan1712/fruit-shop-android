package com.example.myapplication.util;

import android.content.Context;

import com.example.myapplication.R;

/**
 * 根据 JSON 中的图片名称安全地查找 drawable 资源，找不到时使用占位图。
 */
public final class ResUtil {

    private ResUtil() {
    }

    public static int getDrawableId(Context context, String name) {
        if (name == null || name.trim().isEmpty()) {
            return R.drawable.fruit_placeholder;
        }
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return id == 0 ? R.drawable.fruit_placeholder : id;
    }
}
