package com.swan.swanmusicplayer;

import java.io.FileDescriptor;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Music class
 * 
 * @author Suhwan Hwang
 * 
 */
public class Music {
    private static final String TAG = "Music";

    private long mId;           // music id
    private String mData;       // music file path
    private String mTitle;      // music title
    private String mAlbum;      // album name
    private String mArtist;     // artist name
    private boolean mHasAlbumArt;  // has album art

    private static final BitmapFactory.Options BITMAP_OPTIONS = new BitmapFactory.Options();

    static {
        BITMAP_OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
        BITMAP_OPTIONS.inDither = false;
    }

    public Music(long id, String data, String title, String album, String artist) {
        super();
        this.mId = id;
        this.mData = data;
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
    }

    /**
     * get id
     * 
     * @return id
     */
    public long getId() {
        return mId;
    }

    /**
     * set id
     * 
     * @param id
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * get music file path
     * 
     * @return music file path 
     */
    public String getData() {
        return mData;
    }

    /**
     * set music file path
     * 
     * @param data
     */
    public void setData(String data) {
        mData = data;
    }

    /**
     * get title
     * 
     * @return title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * set title
     * 
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * get artist name
     * 
     * @return artist name
     */
    public String getArtist() {
        return mArtist;
    }

    /**
     * set artist name
     * 
     * @param artist
     */
    public void setArtist(String artist) {
        mArtist = artist;
    }

    /**
     * get album name
     * 
     * @return album name
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * set album name
     * 
     * @param album
     */
    public void setAlbum(String album) {
        mAlbum = album;
    }

    /**
     * get album art
     * 
     * @param context
     * @return album art
     */
    public Bitmap getAlbumArt(Context context) {
        Uri uri = Uri.parse("content://media/external/audio/media/" + mId
                + "/albumart");
        if (uri == null)
            return null;

        ContentResolver res = context.getContentResolver();
        mHasAlbumArt = false;
        try {
            ParcelFileDescriptor parcelFileDescriptor = res.openFileDescriptor(
                    uri, "r");
            if (parcelFileDescriptor != null) {
                mHasAlbumArt = true;
                FileDescriptor fileDescriptor = parcelFileDescriptor
                        .getFileDescriptor();
                return BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
                        BITMAP_OPTIONS);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to load album art for " + mTitle, e);
        }

        return null;
    }

    /**
     * make string
     */
    @Override
    public String toString() {
        return mTitle + " - " + mArtist;
    }
}
