package team.fzo.puppas.mini_player.utils;

import java.util.ArrayList;
import java.util.List;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.activities.MainActivity;
import team.fzo.puppas.mini_player.model.MusicList;


public class MusicListUtils {

    private static boolean[] initStatus = new boolean[MainActivity.MUSIC_LIST_CATEGORY_NUM];
    public static List<MusicList> musicLists = new ArrayList<>(MainActivity.MUSIC_LIST_CATEGORY_NUM);
    private static String [] musicListName = new String[]{"我喜欢的音乐","学习专用音乐","治愈系音乐","ACG",
            "华语音乐","古典音乐","流行音乐",
            "轻音乐","爵士音乐","说唱"};

    private static int []musicListAlbumId = new int[]{R.drawable.collect,R.drawable.study_music,
            R.drawable.cure_music,R.drawable.acg_music,R.drawable.chinese_music,
            R.drawable.classic_music,R.drawable.pop_music,R.drawable.light_music,
            R.drawable.jazz_music,R.drawable.rap_music};

    public static void getListContent(){
        MusicList list;
        int i = 0;

        initStatus[0] = true;
        for(int a = 1; a < initStatus.length; a++){
            initStatus[a] = false;
        }

        for(; i < musicListName.length; i++){
            list = new MusicList();
            list.setMusicListAlbumId(musicListAlbumId[i]);
            list.setMusicListName(musicListName[i]);
            list.setSelecterStatus(initStatus[i]);
            list.setMusicListId(i);
            list.save();
            musicLists.add(list);
        }
    }
}
