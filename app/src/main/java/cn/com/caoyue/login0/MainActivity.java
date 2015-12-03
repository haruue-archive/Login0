package cn.com.caoyue.login0;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private String oldUsername = "";
    private String oldPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        //建表
        UserDatabase userDatabase = new UserDatabase(this, "UserDatabase.db", null, 2);
        userDatabase.getWritableDatabase();
        //检测是否登录，没登录就滚去登录
        if (!getIsLogin()) {
            finish();
            LoginActivity.actionStart(MainActivity.this, oldUsername, oldPassword, true);
        } else {
            finish();
            AccountActivity.actionStart(MainActivity.this, oldUsername);
        }
    }

    private boolean getIsLogin() {
        //查询 SharedPreferences
        SharedPreferences reader = getSharedPreferences("userinfo", MODE_PRIVATE);
        String username = reader.getString("username", "");
        this.oldUsername = username;
        String passwordMD5 = reader.getString("password", "").replaceAll(" ", "");
        if (username.isEmpty() || passwordMD5.isEmpty()) {
            return false;
        }
        //查询表
        //获取数据库
        UserDatabase userDatabase = new UserDatabase(MainActivity.this, "UserDatabase.db", null, 2);
        SQLiteDatabase db = userDatabase.getWritableDatabase();
        //检查用户名和密码
        Cursor cursor = db.query("UserDatabase", new String[]{"username,password"}, "username=? and password=?", new String[]{username, passwordMD5}, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
