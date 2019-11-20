package team.fzo.puppas.mini_player.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class MusicList extends LitePalSupport {

    @Column(unique = true, nullable = false)
    private int musicListId;    //歌单的id

    private int musicListAlbumId;      //歌单图片id

    private boolean selectedStatus;         //判断是否添加到歌单

    @Column(defaultValue = "unkonwn")
    private String musicListName;       //歌单名字

    public MusicList() {
    }

    public String getMusicListName(){
        return musicListName;
    }

    public int getMusicListAlbumId(){
        return musicListAlbumId;
    }

    public void setMusicListAlbumId(int musiclistalbumId){
        this.musicListAlbumId =musiclistalbumId;
    }

    public void setMusicListName(String musiclistname){
        this.musicListName =musiclistname;
    }

    public int getMusicListId() {
        return musicListId;
    }

    public void setMusicListId(int musicListId) {
        this.musicListId = musicListId;
    }

    public boolean getSelecterStatus() {
        return selectedStatus;
    }

    public void setSelecterStatus(boolean selecterStatus) {
        selectedStatus = selecterStatus;
    }

}









