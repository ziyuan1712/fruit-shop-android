package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.myapplication.util.SPUtil;

/**
 * 启动页：Logo 与品牌语展示，2 秒倒计时后按登录状态跳转。
 */
public class SplashActivity extends BaseActivity {

    private static final int COUNTDOWN_SECONDS = 2;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean navigated;
    private int remainSeconds = COUNTDOWN_SECONDS;
    private TextView tvCountdown;

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            remainSeconds--;
            if (remainSeconds <= 0) {
                goNext();
                return;
            }
            if (tvCountdown != null) {
                tvCountdown.setText(remainSeconds + " 秒后自动进入");
            }
            handler.postDelayed(this, 1000L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.brand_green);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_splash);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvCountdown.setText(COUNTDOWN_SECONDS + " 秒后自动进入");
        findViewById(R.id.tv_skip).setOnClickListener(v -> goNext());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 启动页等待倒计时结束或点击“跳过”
            }
        });
        handler.postDelayed(tickRunnable, 1000L);
    }

    private void goNext() {
        if (navigated) {
            return;
        }
        navigated = true;
        handler.removeCallbacks(tickRunnable);
        Class<?> target = SPUtil.isLoggedIn() ? MainActivity.class : LoginActivity.class;
        startActivity(new Intent(this, target));
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(tickRunnable);
        super.onDestroy();
    }
}
