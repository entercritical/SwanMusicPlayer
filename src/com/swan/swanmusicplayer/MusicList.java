package com.swan.swanmusicplayer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Music List class 
 * 
 * Singleton class for runtime music data
 * 
 * @author Suhwan Hwang
 *
 */
public class MusicList {
    private static final String TAG = "MusicList";
    private static MusicList uniqueInstance;
    private ArrayList<Music> mMusicList = new ArrayList<Music>();
    
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
    
    private MusicList() {
    }
    
    public static synchronized MusicList getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new MusicList();
        }
        
        return uniqueInstance;
    }
    
    /**
     * get all music data
     * 
     * @param context
     * @return true/false
     */
    public boolean refreshAllMusic(Context context) {
        // Content Resolver
        ContentResolver resolver = context.getContentResolver();
        
        // Get Music Data
        Uri media = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = resolver.query(media, PROJECTION, selection, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "Music List Empty");
            return false;
        }

        mMusicList.clear(); // clear
        
        int count = cursor.getCount();

        for (int i = 0; i != count; ++i) {
            if (!cursor.moveToNext()) {
                break;
            }
            //Log.d(TAG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            // Add to ArrayList
            mMusicList.add(new Music(
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    ));

        }
        cursor.close();
        return true;
    }
    
    /**
     * get ArrayList of music data
     * 
     * @return music data
     */
    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }
}
