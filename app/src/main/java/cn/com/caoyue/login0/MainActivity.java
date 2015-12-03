package cn.com.caoyue.login0;

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
        setContentView(R.layout.activity_main);
        //建表
        UserDatabase userDatabase = new UserDatabase(this, "UserDatabase.db", null, 2);
        userDatabase.getWritableDatabase();
        //检测是否登陆，没登陆就滚去登陆
        if (!getIsLogin()) {
            ((TextView) findViewById(R.id.tip_is_login)).setText(R.string.tip_login_first);
            LoginActivity.actionStart(MainActivity.this, oldUsername, oldPassword, true);
        }
        //Toolbar 显示
        Toolbar toolbarWithBack = (Toolbar) findViewById(R.id.toolbar_with_back_inMain);
        setSupportActionBar(toolbarWithBack);
    }

    private boolean getIsLogin() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
