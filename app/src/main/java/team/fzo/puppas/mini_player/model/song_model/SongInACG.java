package team.fzo.puppas.mini_player.model.song_model;

import team.fzo.puppas.mini_player.model.Song;

public class SongInACG extends Song {
    public SongInACG(Song song){
        this.songId = song.getSongId();
        this.albumId = song.getAlbumId();
        this.size = song.getSize();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.duration = song.getDuration();
        this.path = song.getPath();
        this.isMusic = song.getIsMusic();
    }
}
