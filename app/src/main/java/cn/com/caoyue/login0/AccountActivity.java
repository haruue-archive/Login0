package cn.com.caoyue.login0;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_account);
        //Toolbar 相关
        Toolbar toolbarWithBack = (Toolbar) findViewById(R.id.toolbar_with_back_inAccount);
        toolbarWithBack.setTitle(R.string.login);
        setSupportActionBar(toolbarWithBack);
        toolbarWithBack.setNavigationIcon(R.mipmap.return_icon);
        toolbarWithBack.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
            }
        });
        //昵称显示
        final String username = getIntent().getStringExtra("username");
        UserDatabase userDatabase = new UserDatabase(AccountActivity.this, "UserDatabase.db", null, 2);
        SQLiteDatabase db = userDatabase.getWritableDatabase();
        Cursor cursor = db.query("UserDatabase", new String[]{"username", "nickname"}, "username=?", new String[]{username}, null, null, null);
        if (!cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), R.string.database_error, Toast.LENGTH_LONG).show();
            cursor.close();
            finish();
            LoginActivity.actionStart(AccountActivity.this, username, "", true);
            return;
        }
        String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        ((TextView) findViewById(R.id.account_info_username)).setText(nickname);
        //[登出]按钮
        ((Button) findViewById(R.id.button_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除 SharedPreferences 信息
                SharedPreferences.Editor editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
                editor.remove("username");
                editor.remove("password");
                editor.apply();
                Toast.makeText(getApplicationContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
                //返回登陆界面
                finish();
                LoginActivity.actionStart(AccountActivity.this, username, "", true);
            }
        });
        //[彻底删除帐号]按钮
        ((Button) findViewById(R.id.button_delete_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle(R.string.delete_account);
                builder.setMessage(R.string.delete_account_warning);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: 从数据表中删除用户

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.delete_account_success, Toast.LENGTH_SHORT).show();
                        AccountActivity.this.finish();
                        LoginActivity.actionStart(AccountActivity.this, "", "", true);
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    //活动启动器
    public static void actionStart(Context context, String username) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }
}
