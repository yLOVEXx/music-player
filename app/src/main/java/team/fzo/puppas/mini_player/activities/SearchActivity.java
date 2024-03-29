package team.fzo.puppas.mini_player.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.litepal.LitePal;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.utils.MusicContentUtils;
import team.fzo.puppas.mini_player.view.RadarView;

public class SearchActivity extends AppCompatActivity {

    private RadarView mRadarView;
    private Button mStartButton;
    private boolean mScanFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRadarView = findViewById(R.id.radar);
        mStartButton = findViewById(R.id.btn_start);
        mScanFlag = false;

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setEnabled(false);
                mRadarView.start();
                getPermissionAndContent();
            }
        });

    }


    private void getPermissionAndContent(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            mScanFlag = true;
            LitePal.deleteAll(Song.class, "songId > ?", "0");
            MusicContentUtils.getContentFromStorage(this);
            mRadarView.stop();
            Toast.makeText(this, "扫描完成", Toast.LENGTH_SHORT).show();
            mStartButton.setEnabled(true);
            mScanFlag = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mScanFlag = true;
                    LitePal.deleteAll(Song.class, "songId > ?", "0");
                    MusicContentUtils.getContentFromStorage(this);
                    mRadarView.stop();
                    Toast.makeText(this, "扫描完成", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "您拒绝了请求", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }

        mStartButton.setEnabled(true);
        mScanFlag = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mScanFlag){
            MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
            mRadarView.stop();
            moveTaskToBack(true);
            //finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
