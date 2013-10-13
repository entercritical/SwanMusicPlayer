package com.swan.swanmusicplayer;

import android.provider.MediaStore;

public class Music {
    public static final String[] PROJECTION = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
    };

    private String mId;
    private String mData;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    
    public Music(String id, String data, String title, String album,
            String artist) {
        super();
        this.mId = id;
        this.mData = data;
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
    }
    
    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }
    public String getData() {
        return mData;
    }
    public void setData(String data) {
        mData = data;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public String getArtist() {
        return mArtist;
    }
    public void setArtist(String artist) {
        mArtist = artist;
    }
    public String getAlbum() {
        return mAlbum;
    }
    public void setAlbum(String album) {
        mAlbum = album;
    }
}
