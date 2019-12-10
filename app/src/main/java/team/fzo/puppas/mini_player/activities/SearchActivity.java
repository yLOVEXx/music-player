package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.view.RadarView;

public class SearchActivity extends AppCompatActivity {

    private RadarView radarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        radarView = findViewById(R.id.radar);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radarView.start();

            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            //此处写退向后台的处理
            MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
            radarView.stop();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
