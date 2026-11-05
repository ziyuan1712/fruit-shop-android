package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.myapplication.util.SPUtil;

/**
 * 所有页面的基类：
 * 1. 统一状态栏 / 导航栏沉浸式适配；
 * 2. 初始化 SharedPreferences；
 * 3. 表单页面可按需响应软键盘高度。
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtil.init(getApplicationContext());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.setAppearanceLightNavigationBars(true);
        } else {
            // API 24/25 不支持深色导航栏图标，改用深绿色导航栏保证可见性
            getWindow().setNavigationBarColor(
                    ContextCompat.getColor(this, R.color.brand_green_dark));
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applySystemBarInsets();
    }

    /**
     * 表单页（登录、确认订单）返回 true，避免软键盘遮挡输入框。
     */
    protected boolean adjustForIme() {
        return false;
    }

    private void applySystemBarInsets() {
        final View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            int bottom = bars.bottom;
            if (adjustForIme()) {
                bottom = Math.max(bottom, windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom);
            }
            view.setPadding(bars.left, bars.top, bars.right, bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(content);
    }
}
