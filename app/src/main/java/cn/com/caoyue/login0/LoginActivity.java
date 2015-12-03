package cn.com.caoyue.login0;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_login);
        //初始化界面
        //Toolbar 相关
        Toolbar toolbarWithBack = (Toolbar) findViewById(R.id.toolbar_with_back_inLogin);
        toolbarWithBack.setTitle(R.string.login);
        setSupportActionBar(toolbarWithBack);
        toolbarWithBack.setNavigationIcon(R.mipmap.return_icon);
        toolbarWithBack.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getBooleanExtra("isBackToExit", true)) {
                    ActivityCollector.finishAll();
                } else {
                    finish();
                }
            }
        });
        //控件相关
        final EditText usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        usernameEditText.setText(getIntent().getStringExtra("username"));
        passwordEditText.setText(getIntent().getStringExtra("password"));
        //输入时“手机号”的显示
        usernameEditText.addTextChangedListener(new TextWatcher() {
            TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tipOnUsername.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorAccent));
                if (s.toString().isEmpty() && count == 0) {
                    tipOnUsername.setText(R.string.blank);
                } else {
                    tipOnUsername.setText(R.string.mobile_number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //输入时“密码”的显示
        passwordEditText.addTextChangedListener(new TextWatcher() {
            TextView tipOnPassword = (TextView) findViewById(R.id.tip_on_password);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tipOnPassword.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorAccent));
                if (s.toString().isEmpty() && count == 0) {
                    tipOnPassword.setText(R.string.blank);
                } else {
                    tipOnPassword.setText(R.string.password);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //在密码框上按下回车键设置
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        //[立即用手机号注册]按钮
        findViewById(R.id.textview_check_to_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.actionStart(LoginActivity.this);
            }
        });
        //[登录]按钮
        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
        //[忘记密码]按钮
        findViewById(R.id.button_forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.tip_reset_password, Toast.LENGTH_LONG).show();
            }
        });
    }

    //活动启动器
    public static void actionStart(Context context, String defaultUsername, String defaultPassword, Boolean isBackToExit) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("username", defaultUsername);
        intent.putExtra("password", defaultPassword);
        intent.putExtra("isBackToExit", isBackToExit);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回键功能设定
        if (keyCode == KeyEvent.KEYCODE_BACK && getIntent().getBooleanExtra("isBackToExit", true)) {
            ActivityCollector.finishAll();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void login(String username, String password) {
        //检查用户名密码是否为空
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.login_error_username_or_psw_empty, Toast.LENGTH_SHORT).show();
            if (username.isEmpty()) {
                TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);
                tipOnUsername.setTextColor(this.getResources().getColor(R.color.colorWarning));
                tipOnUsername.setText(R.string.tip_username_empty);
            }
            if (password.isEmpty()) {
                TextView tipOnPassword = (TextView) findViewById(R.id.tip_on_password);
                tipOnPassword.setTextColor(this.getResources().getColor(R.color.colorWarning));
                tipOnPassword.setText(R.string.tip_password_empty);
            }
            return;
        }
        //密码MD5处理
        String passwordMD5;
        try {
            passwordMD5 = com.jude.utils.JUtils.MD5(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), R.string.password_MD5_error, Toast.LENGTH_LONG).show();
            Log.e("passwordMD5_Error", e.toString());
            return;
        }
        //获取数据库
        UserDatabase userDatabase = new UserDatabase(LoginActivity.this, "UserDatabase.db", null, 2);
        SQLiteDatabase db = userDatabase.getWritableDatabase();
        //检查用户名和密码
        Cursor cursor = db.query("UserDatabase", new String[]{"username,password"}, "username=? and password=?", new String[]{username, passwordMD5}, null, null, null);
        if (cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.login_failed_username_or_password_error, Toast.LENGTH_LONG).show();
            TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);
            tipOnUsername.setTextColor(this.getResources().getColor(R.color.colorWarning));
            tipOnUsername.setText(R.string.login_failed_username_or_password_error);
            cursor.close();
            return;
        }
        cursor.close();
    }
}
