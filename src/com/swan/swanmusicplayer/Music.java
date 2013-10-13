package com.swan.swanmusicplayer;

import java.io.FileDescriptor;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class Music {
    private static final String TAG = "Music";
    
    private long mId;
    private String mData;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    private boolean mHasCover;
    
    private static final BitmapFactory.Options BITMAP_OPTIONS = new BitmapFactory.Options();

    static {
        BITMAP_OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
        BITMAP_OPTIONS.inDither = false;
    }
    
    public Music(long id, String data, String title, String album,
            String artist) {
        super();
        this.mId = id;
        this.mData = data;
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
    }
    
    public long getId() {
        return mId;
    }
    public void setId(long id) {
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

    public Bitmap getCover(Context context) {
        Uri uri = Uri.parse("content://media/external/audio/media/" + mId + "/albumart");
        if (uri == null)
            return null;

        ContentResolver res = context.getContentResolver();
        mHasCover = false;
        try {
            ParcelFileDescriptor parcelFileDescriptor = res.openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                mHasCover = true;
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, BITMAP_OPTIONS);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to load cover art for " + mTitle, e);
        }

        return null;
    }

    @Override
    public String toString() {
        return mTitle + " - " + mArtist;
    }
}
