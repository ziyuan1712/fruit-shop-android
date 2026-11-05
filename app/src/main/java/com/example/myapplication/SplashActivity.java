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
 * 启动页：
 * - 未登录时直接进入登录页，不先播倒计时；
 * - 登录成功后展示 3 秒欢迎倒计时，再进入首页；
 * - 已记住登录的用户启动时直接倒计时进入首页。
 */
public class SplashActivity extends BaseActivity {

    public static final String EXTRA_FROM_LOGIN = "from_login";
    private static final int COUNTDOWN_SECONDS = 3;

    private boolean navigated;
    private boolean fromLogin;
    private CountDownTimer countDownTimer;
    private TextView tvCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 未登录用户优先看到登录页，不让他们先等 3 秒倒计时。
        fromLogin = getIntent().getBooleanExtra(EXTRA_FROM_LOGIN, false);
        if (!fromLogin && !SPUtil.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
        Class<?> target = (fromLogin || SPUtil.isLoggedIn())
                ? MainActivity.class : LoginActivity.class;
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
