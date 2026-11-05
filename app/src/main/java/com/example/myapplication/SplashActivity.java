package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.myapplication.util.SPUtil;

/**
 * 启动页：Logo 与品牌语展示，3 秒倒计时后按登录状态跳转。
 */
public class SplashActivity extends BaseActivity {

    private static final int COUNTDOWN_SECONDS = 3;

    private boolean navigated;
    private CountDownTimer countDownTimer;
    private TextView tvCountdown;

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
                finish();
            }
        });
        startCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_SECONDS * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) Math.ceil(millisUntilFinished / 1000.0);
                if (tvCountdown != null) {
                    tvCountdown.setText(seconds + " 秒后自动进入");
                }
            }

            @Override
            public void onFinish() {
                goNext();
            }
        };
        countDownTimer.start();
    }

    private void goNext() {
        if (navigated) {
            return;
        }
        navigated = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Class<?> target = SPUtil.isLoggedIn() ? MainActivity.class : LoginActivity.class;
        startActivity(new Intent(this, target));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
