package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;

public class ManageMusicListActivity extends PlayActivity {

    private boolean[] checkedArray = new boolean[MainActivity.MUSIC_LIST_CATEGORY_NUM];
    public List<MusicList> musicLists = new ArrayList<>(MainActivity.MUSIC_LIST_CATEGORY_NUM);
    private Button mFinish_Bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_music_list);
        mFinish_Bt = findViewById(R.id.finish);
        musicLists =LitePal.findAll(MusicList.class);

        for(int i=0;i<checkedArray.length;i++){
            checkedArray[i] = musicLists.get(i).getSelecterStatus();
        }

        final CheckBox[] checkBoxes = new CheckBox[]
                {
                        findViewById(R.id.checkBox1),findViewById(R.id.checkBox2),
                        findViewById(R.id.checkBox3),findViewById(R.id.checkBox4),
                        findViewById(R.id.checkBox5),findViewById(R.id.checkBox6),
                        findViewById(R.id.checkBox7),findViewById(R.id.checkBox8),
                        findViewById(R.id.checkBox9),findViewById(R.id.checkBox10)
                };

        for( int i=0; i<checkBoxes.length; i++){
            checkBoxes[i].setText(musicLists.get(i).getMusicListName());
            checkBoxes[i].setId(musicLists.get(i).getMusicListId());
            checkBoxes[i].setChecked(checkedArray[i]);
        }

        checkBoxes[0].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[0] = isChecked;
            }
        });

        checkBoxes[1].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[1] = isChecked;
            }
        });
        checkBoxes[2].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[2] = isChecked;
            }
        });
        checkBoxes[3].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[3] = isChecked;
            }
        });
        checkBoxes[4].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[4] = isChecked;
            }
        });
        checkBoxes[5].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[5] = isChecked;
            }
        });
        checkBoxes[6].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[6] = isChecked;
            }
        });
        checkBoxes[7].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[7] = isChecked;
            }
        });
        checkBoxes[8].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[8] = isChecked;
            }
        });
        checkBoxes[9].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedArray[9] = isChecked;
            }
        });

        //设置点击事件
        mFinish_Bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetSelectedStatus(checkedArray);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    //改变选中项目的状态
    public void SetSelectedStatus(boolean[] checkedArray){
        for(int i = 0; i<checkedArray.length; i++){
            if(checkedArray[i]==true){
                musicLists.get(i).setSelecterStatus(true);
                musicLists.get(i).save();
            }
            else{
                musicLists.get(i).setSelecterStatus(false);
                musicLists.get(i).save();
            }
        }
    }

}
