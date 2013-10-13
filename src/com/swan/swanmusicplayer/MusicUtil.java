package com.swan.swanmusicplayer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Class MusicUtil
 * 
 * Singleton class for Music data
 * 
 * @author Suhwan Hwang
 *
 */
public class MusicUtil {
    private static final String TAG = "MusicUtil";
    
    private static MusicUtil mMusicUtil;
    private Context mContext;    
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
    
    private MusicUtil(Context context) {
        mContext = context;
    }
    
    public static synchronized MusicUtil getInstance(Context context) {
        if (mMusicUtil == null) {
            mMusicUtil = new MusicUtil(context); 
        }
        
        return mMusicUtil;
    }
    
    /**
     * Music List 
     * @return Music ArrayList
     */
    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }
    
    public void resolveMusicList() {
        ContentResolver resolver = mContext.getContentResolver();
        
        Uri media = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = resolver.query(media, PROJECTION, selection, null, null);
        if (cursor == null || cursor.getCount() == 0) {
        }

        int count = cursor.getCount();

        for (int i = 0; i != count; ++i) {
            if (!cursor.moveToNext()) {
                break;
            }
            Log.d(TAG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mMusicList.add(new Music(
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    ));

        }
        cursor.close();
    }
}
