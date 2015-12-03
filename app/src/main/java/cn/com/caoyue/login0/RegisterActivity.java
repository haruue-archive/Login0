package cn.com.caoyue.login0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_register);
        //初始化界面
        final EditText nicknameEditText = (EditText) findViewById(R.id.nickname_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
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
        //[注册]按钮
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(nicknameEditText.getText().toString(), passwordEditText.getText().toString());
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

    private void register(String nickname, String password) {
        if (nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.login_error_nickname_or_psw_empty, Toast.LENGTH_SHORT).show();
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
    }
}
