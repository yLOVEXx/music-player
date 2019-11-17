package team.fzo.puppas.mini_player.model;

public class MusicListAlbum {
    private String name;
    private int imageId;

    public MusicListAlbum(String name, int imageId){
        this.name=name;
        this.imageId=imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setImageId(int imageId){
        this.imageId=imageId;
    }
}
