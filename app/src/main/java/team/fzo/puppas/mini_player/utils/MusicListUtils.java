package team.fzo.puppas.mini_player.utils;

import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.MusicList;


public class MusicListUtils {
    public static final int MUSIC_LIST_CATEGORY_NUM = 11;
    private static int[] sInitStatus = new int[MUSIC_LIST_CATEGORY_NUM];

    private static String [] sMusicListName = new String[]{
            "我喜欢的音乐","历史播放","学习专用音乐","治愈系音乐","ACG",
            "华语音乐","古典音乐","流行音乐",
            "轻音乐","爵士音乐","说唱"};

    private static int [] sMusicListAlbumId = new int[]{R.drawable.album_collect,R.drawable.album_recent_music,
            R.drawable.album_study_music, R.drawable.album_cure_music,R.drawable.album_acg_music,
            R.drawable.album_chinese_music, R.drawable.album_classic_music,R.drawable.album_pop_music,
            R.drawable.album_light_music, R.drawable.album_jazz_music,R.drawable.album_rap_music};


    //加载所有歌单到数据库
    public static void getListContent(){
        MusicList list;
       sInitStatus[0] = 1;
       sInitStatus[1] = 1;
        for(int i = 0; i < sInitStatus.length; i++){
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
