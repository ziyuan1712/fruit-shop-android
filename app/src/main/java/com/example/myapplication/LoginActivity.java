package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.SPUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 登录 / 注册页面：
 * - 输入校验（用户名长度、密码长度、两次密码一致）
 * - SharedPreferences 保存账号与登录状态
 * - “记住登录状态”控制下次启动是否自动登录
 */
public class LoginActivity extends BaseActivity {

    private static final String STATE_REGISTER_MODE = "registerMode";

    private TextInputLayout tilUsername, tilPwd, tilConfirm;
    private TextInputEditText etUsername, etPwd, etConfirm;
    private MaterialCheckBox cbRemember;
    private MaterialButton btnSubmit;
    private TextView tvFormTitle, tvFormSub, tvSwitchMode, tvDemo;

    private boolean registerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUsername = findViewById(R.id.til_username);
        tilPwd = findViewById(R.id.til_pwd);
        tilConfirm = findViewById(R.id.til_confirm);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        etConfirm = findViewById(R.id.et_confirm);
        cbRemember = findViewById(R.id.cb_remember);
        btnSubmit = findViewById(R.id.btn_submit);
        tvFormTitle = findViewById(R.id.tv_form_title);
        tvFormSub = findViewById(R.id.tv_form_sub);
        tvSwitchMode = findViewById(R.id.tv_switch_mode);
        tvDemo = findViewById(R.id.tv_demo);

        String lastUser = SPUtil.getLastUsername();
        if (!TextUtils.isEmpty(lastUser)) {
            etUsername.setText(lastUser);
            etPwd.requestFocus();
        }

        btnSubmit.setOnClickListener(v -> submit());
        tvSwitchMode.setOnClickListener(v -> switchMode(!registerMode));
        etPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit();
                return true;
            }
            return false;
        });
        etConfirm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit();
                return true;
            }
            return false;
        });

        if (savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_REGISTER_MODE, false)) {
            switchMode(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_REGISTER_MODE, registerMode);
    }

    @Override
    protected boolean adjustForIme() {
        return true;
    }

    private void switchMode(boolean toRegister) {
        registerMode = toRegister;
        clearErrors();
        if (registerMode) {
            tvFormTitle.setText("创建账号");
            tvFormSub.setText("注册后开启你的鲜果生活");
            tilConfirm.setVisibility(View.VISIBLE);
            cbRemember.setVisibility(View.GONE);
            btnSubmit.setText("注册");
            tvSwitchMode.setText("已有账号？去登录");
            tvDemo.setVisibility(View.GONE);
            etPwd.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            tvFormTitle.setText("欢迎回来");
            tvFormSub.setText("登录后同步购物车与订单");
            tilConfirm.setVisibility(View.GONE);
            cbRemember.setVisibility(View.VISIBLE);
            btnSubmit.setText("登录");
            tvSwitchMode.setText("还没有账号？立即注册");
            tvDemo.setVisibility(View.VISIBLE);
            etPwd.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    private void submit() {
        clearErrors();
        String username = textOf(etUsername);
        String password = textOf(etPwd);

        if (username.length() < 3) {
            tilUsername.setError("用户名至少 3 个字符");
            return;
        }
        if (username.contains(" ")) {
            tilUsername.setError("用户名不能包含空格");
            return;
        }
        if (password.length() < 6) {
            tilPwd.setError("密码至少 6 位");
            return;
        }

        if (registerMode) {
            String confirm = textOf(etConfirm);
            if (!password.equals(confirm)) {
                tilConfirm.setError("两次输入的密码不一致");
                return;
            }
            if (SPUtil.hasAccount(username)) {
                tilUsername.setError("该用户名已被注册");
                return;
            }
            if (!SPUtil.register(username, password)) {
                Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                return;
            }
            // 注册只创建账号，不建立登录会话；回到登录态由用户手动输入密码登录。
            switchMode(false);
            etUsername.setText(username);
            etPwd.setText("");
            etConfirm.setText("");
            etPwd.requestFocus();
            Toast.makeText(this, "注册成功，请使用新账号登录", Toast.LENGTH_SHORT).show();
        } else {
            if (!SPUtil.checkPassword(username, password)) {
                tilPwd.setError("用户名或密码错误");
                return;
            }
            SPUtil.saveSession(username, cbRemember.isChecked());
            Toast.makeText(this, "欢迎回来，" + username, Toast.LENGTH_SHORT).show();
            enterMain();
        }
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilPwd.setError(null);
        tilConfirm.setError(null);
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void enterMain() {
        hideKeyboard();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }
}
