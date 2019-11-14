package com.sample.andremion.musicplayer.activities;

public class Music {
    private String name;
    private int imageId;
    private String number;

    public Music(String name,int imageId,String number){
        this.name=name;
        this.imageId=imageId;
        this.number=number;
    }


    public String getNumber(){
        return number;
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

    public void setNumber(String number){
        this.number=number;
    }

    public void setImageId(int imageId){
        this.imageId=imageId;
    }
}
