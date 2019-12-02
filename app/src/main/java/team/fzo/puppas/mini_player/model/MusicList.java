package team.fzo.puppas.mini_player.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class MusicList extends LitePalSupport {

    @Column(unique = true, nullable = false)
    private int musicListId;    //歌单的id

    private int musicListAlbumId;      //歌单图片id

    private int selectedStatus;         //判断是否添加到歌单

    @Column(defaultValue = "unkonwn")
    private String musicListName;       //歌单名字

    @Column(defaultValue = "0")
    private int songNum;                //歌曲数目


    public String getMusicListName(){
        return musicListName;
    }

    public int getMusicListAlbumId(){
        return musicListAlbumId;
    }

    public void setMusicListAlbumId(int musicListAlbumId){
        this.musicListAlbumId = musicListAlbumId;
    }

    public void setMusicListName(String musicListName){
        this.musicListName = musicListName;
    }

    public int getMusicListId() {
        return musicListId;
    }

    public void setMusicListId(int musicListId) {
        this.musicListId = musicListId;
    }

    public int getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(int selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public int getSongNum() {
        return songNum;
    }

    public void setSongNum(int songNum) {
        this.songNum = songNum;
    }
}









