package cn.com.caoyue.login0;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_register);
        //初始化界面
        final EditText usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        final EditText nicknameEditText = (EditText) findViewById(R.id.nickname_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        //输入时“手机号”的显示
        usernameEditText.addTextChangedListener(new TextWatcher() {
            TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tipOnUsername.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorAccent));
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
        //输入时“昵称”的显示
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            TextView tipOnNickname = (TextView) findViewById(R.id.tip_on_nickname);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tipOnNickname.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorAccent));
                if (s.toString().isEmpty() && count == 0) {
                    tipOnNickname.setText(R.string.blank);
                } else {
                    tipOnNickname.setText(R.string.nickname);
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
                tipOnPassword.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorAccent));
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
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    register(usernameEditText.getText().toString(), nicknameEditText.getText().toString(), passwordEditText.getText().toString());
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        });
        //[注册]按钮
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(usernameEditText.getText().toString(), nicknameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    //活动启动器
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void register(String username, String nickname, String password) {
        if (nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.login_error_nickname_or_psw_empty, Toast.LENGTH_SHORT).show();
            if (username.isEmpty()) {
                TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);
                tipOnUsername.setTextColor(this.getResources().getColor(R.color.colorWarning));
                tipOnUsername.setText(R.string.tip_username_empty);
            }
            if (nickname.isEmpty()) {
                TextView tipOnNickname = (TextView) findViewById(R.id.tip_on_nickname);
                tipOnNickname.setTextColor(this.getResources().getColor(R.color.colorWarning));
                tipOnNickname.setText(R.string.tip_nickname_empty);
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
        UserDatabase userDatabase = new UserDatabase(RegisterActivity.this, "UserDatabase.db", null, 2);
        SQLiteDatabase db = userDatabase.getWritableDatabase();
        //检查手机号重复
        Cursor cursor = db.query("UserDatabase", new String[]{"username"}, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), R.string.username_already_exists, Toast.LENGTH_SHORT).show();
            TextView tipOnUsername = (TextView) findViewById(R.id.tip_on_username);
            tipOnUsername.setTextColor(this.getResources().getColor(R.color.colorWarning));
            tipOnUsername.setText(R.string.username_already_exists);
            cursor.close();
            return;
        }
        cursor.close();
        //插入数据
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("nickname", nickname);
        values.put("password", passwordMD5);
        db.insert("UserDatabase", null, values);
        values.clear();
        Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //返回键功能设定
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            ActivityCollector.finishAll();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
