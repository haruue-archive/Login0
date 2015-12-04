package cn.com.caoyue.login0;

import android.app.Dialog;
import android.content.ContentValues;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_account);
        //Toolbar 相关
        Toolbar toolbarWithBack = (Toolbar) findViewById(R.id.toolbar_with_back_inAccount);
        toolbarWithBack.setTitle(R.string.account_manage);
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
        //[退出程序]按钮
        ((Button) findViewById(R.id.button_exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
            }
        });
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
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //从数据表中删除用户
                        UserDatabase userDatabase = new UserDatabase(AccountActivity.this, "UserDatabase.db", null, 2);
                        SQLiteDatabase db = userDatabase.getWritableDatabase();
                        db.delete("UserDatabase", "username=?", new String[]{username});
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.delete_account_success, Toast.LENGTH_SHORT).show();
                        AccountActivity.this.finish();
                        LoginActivity.actionStart(AccountActivity.this, "", "", true);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        //[密码修改]按钮
        ((Button) findViewById(R.id.button_change_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle(R.string.change_password);
                final View passwordChangeView = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_change_password, null);
                builder.setView(passwordChangeView);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPassword = ((EditText) passwordChangeView.findViewById(R.id.old_password_edit_text)).getText().toString();
                        String newPassword = ((EditText) passwordChangeView.findViewById(R.id.new_password_edit_text)).getText().toString();
                        String newPasswordAgain = ((EditText) passwordChangeView.findViewById(R.id.new_password_again_edit_text)).getText().toString();
                        //检查低级错误
                        if (oldPassword.isEmpty() || newPassword.isEmpty() || newPasswordAgain.isEmpty()) {
                            Toast.makeText(getApplicationContext(), R.string.change_password_error_input_empty, Toast.LENGTH_SHORT).show();
                        }
                        if (!newPassword.equals(newPasswordAgain)) {
                            Toast.makeText(getApplicationContext(), R.string.error_password_again_diff, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (oldPassword.equals(newPassword)) {
                            Toast.makeText(getApplicationContext(), R.string.error_old_new_password_same, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //密码MD5处理
                        String oldPasswordMD5, newPasswordMD5;
                        try {
                            oldPasswordMD5 = com.jude.utils.JUtils.MD5(oldPassword.getBytes("UTF-8")).replaceAll(" ", "");
                            newPasswordMD5 = com.jude.utils.JUtils.MD5(newPassword.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(getApplicationContext(), R.string.password_MD5_error, Toast.LENGTH_LONG).show();
                            Log.e("passwordMD5_Error", e.toString());
                            return;
                        }
                        //打开数据库
                        UserDatabase userDatabase = new UserDatabase(AccountActivity.this, "UserDatabase.db", null, 2);
                        SQLiteDatabase db = userDatabase.getWritableDatabase();
                        //检查旧密码
                        Cursor cursor = db.query("UserDatabase", new String[]{"username,password"}, "username=? and password=?", new String[]{username, oldPasswordMD5}, null, null, null);
                        if (!cursor.moveToFirst()) {
                            Toast.makeText(getApplicationContext(), R.string.error_old_password, Toast.LENGTH_SHORT).show();
                            cursor.close();
                            return;
                        }
                        cursor.close();
                        //修改密码
                        ContentValues values = new ContentValues();
                        values.put("password", newPasswordMD5);
                        db.update("UserDatabase", values, "username=?", new String[]{username});
                        Toast.makeText(getApplicationContext(), R.string.password_change_success, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
