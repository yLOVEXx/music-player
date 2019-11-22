package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import org.litepal.LitePal;

import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;
import team.fzo.puppas.mini_player.utils.MusicListUtils;

public class ManageMusicListActivity extends PlayActivity {

    private int[] mCheckedArray = new int[MusicListUtils.MUSIC_LIST_CATEGORY_NUM-2];
    private List<MusicList> mMusicLists;
    private Button mFinishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_music_list);
        mFinishBtn = findViewById(R.id.finish);
        mMusicLists = LitePal.findAll(MusicList.class);

        for(int i = 0; i < mCheckedArray.length; i++){
            mCheckedArray[i] = mMusicLists.get(i+2).getSelectedStatus();

        }

        final CheckBox[] checkBoxes = new CheckBox[]{
                        findViewById(R.id.checkBox1),findViewById(R.id.checkBox2),
                        findViewById(R.id.checkBox3),findViewById(R.id.checkBox4),
                        findViewById(R.id.checkBox5),findViewById(R.id.checkBox6),
                        findViewById(R.id.checkBox7),findViewById(R.id.checkBox8),
                        findViewById(R.id.checkBox9)
        };
                        //findViewById(R.id.checkBox10),findViewById(R.id.checkBox11)

        for( int i = 0; i < checkBoxes.length; i++){
            checkBoxes[i].setText(mMusicLists.get(i+2).getMusicListName());

            checkBoxes[i].setId(mMusicLists.get(i+2).getMusicListId());
            if(mCheckedArray[i] == 1)
                checkBoxes[i].setChecked(true);
            else
                checkBoxes[i].setChecked(false);

            checkBoxes[i].setOnCheckedChangeListener(new CheckedChangeListener(i));
        }



        //设置点击事件
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedStatus(mCheckedArray);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    private class CheckedChangeListener implements OnCheckedChangeListener{
        private int i;
        public CheckedChangeListener(int i ){
            this.i = i;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            compoundButton.setChecked(b);
            mCheckedArray[i] = b ? 1 : 0;
        }
    }

    //改变选中项目的状态
    public void setSelectedStatus(int[] checkedArray){
        for(int i = 0; i < checkedArray.length; i++){
            mMusicLists.get(i+2).setSelectedStatus(checkedArray[i]);
            mMusicLists.get(i+2).save();
        }
    }

}
