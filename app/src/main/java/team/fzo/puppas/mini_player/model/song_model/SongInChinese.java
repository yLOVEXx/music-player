package team.fzo.puppas.mini_player.model.song_model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class SongInChinese extends LitePalSupport {
    @Column(unique = true, nullable = false)
    private long id;           //歌曲id

    private long albumId;      //专辑id
    private long size;         //歌曲大小

    @Column(defaultValue = "unkonwn")
    private String name;

    @Column(defaultValue = "unkonwn")
    private String artist;

    private long duration;

    @Column(nullable = false)
    private String path;

    private int isMusic;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }
}
