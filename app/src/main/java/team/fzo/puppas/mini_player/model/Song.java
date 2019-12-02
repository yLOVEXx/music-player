package team.fzo.puppas.mini_player.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {
    @Column(unique = true, nullable = false)
    protected long songId;           //歌曲id

    protected long albumId;      //专辑id
    protected long size;         //歌曲大小

    @Column(defaultValue = "unkonwn")
    protected String name;

    @Column(defaultValue = "unkonwn")
    protected String artist;

    protected long duration;

    @Column(nullable = false)
    protected String path;

    protected int isMusic;

    public Song(){

    }

    public Song(Song song){
        this.songId = song.getSongId();
        this.albumId = song.getAlbumId();
        this.size = song.getSize();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.duration = song.getDuration();
        this.path = song.getPath();
        this.isMusic = song.getIsMusic();
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
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
