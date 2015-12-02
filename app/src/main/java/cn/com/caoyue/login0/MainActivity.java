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
        Toolbar toolbarWithBack = (Toolbar) findViewById(R.id.toolbar_with_back_inMain);
        setSupportActionBar(toolbarWithBack);
        if (!getIsLogin()) {
            ((TextView) findViewById(R.id.tip_is_login)).setText(R.string.tip_login_first);
            LoginActivity.actionStart(MainActivity.this, oldUsername, oldPassword, true);
        }
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
