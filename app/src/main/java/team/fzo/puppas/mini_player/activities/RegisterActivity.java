package team.fzo.puppas.mini_player.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import team.fzo.puppas.mini_player.R;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register_step_two);

        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        EditText pwd1 = findViewById(R.id.et_register_pwd_input);
        EditText pwd2 = findViewById(R.id.et_register_pwd_input2);
        Button bt_sumit = findViewById(R.id.bt_register_submit);


        if(pwd1.getFreezesText() == pwd2.getFreezesText()){
            bt_sumit.setBackgroundColor(R.drawable.bg_login_submit);
            bt_sumit.setTextColor(getResources().getColor(R.color.white));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            //此处写退向后台的处理
            MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}