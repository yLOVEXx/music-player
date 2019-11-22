package team.fzo.puppas.mini_player.utils;

import android.util.Log;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;


public class MusicListUtils {
    public static final int MUSIC_LIST_CATEGORY_NUM = 11;
    private static int[] sInitStatus = new int[MUSIC_LIST_CATEGORY_NUM];

    private static String [] sMusicListName = new String[]{
            "最近播放","我喜欢的音乐","学习专用音乐","治愈系音乐","ACG",
            "华语音乐","古典音乐","流行音乐",
            "轻音乐","爵士音乐","说唱"};

    private static int [] sMusicListAlbumId = new int[]{R.drawable.recent_music,R.drawable.collect,
            R.drawable.study_music, R.drawable.cure_music,R.drawable.acg_music,
            R.drawable.chinese_music, R.drawable.classic_music,R.drawable.pop_music,
            R.drawable.light_music, R.drawable.jazz_music,R.drawable.rap_music};


    //加载所有歌单到数据库
    public static void getListContent(){
        MusicList list;
        sInitStatus[0] = 1;
        sInitStatus[1] = 1;
        for(int i = 2; i < sInitStatus.length; i++){
            sInitStatus[i] = 0;
        }

        for(int i = 0; i < sMusicListName.length; i++){
            list = new MusicList();
            list.setMusicListAlbumId(sMusicListAlbumId[i]);
            list.setMusicListName(sMusicListName[i]);
            list.setSelectedStatus(sInitStatus[i]);
            list.setMusicListId(i);
            list.save();
        }
    }
}
