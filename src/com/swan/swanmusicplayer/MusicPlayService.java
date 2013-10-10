package com.swan.swanmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayService extends Service implements OnErrorListener {
    private static final String TAG = "PlaybackService";
    
    public static final String ACTION_PLAY = "com.swan.action.PLAY";
    public static final String ACTION_PAUSE = "com.swan.action.PAUSE";
    public static final String ACTION_NEXT_SONG = "com.swan.action.NEXT_SONG";
    public static final String ACTION_PREV_SONG = "com.swan.action.PREV_SONG";
    
    private MediaPlayer mPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        
//        Uri myUri = ....; // initialize Uri here
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setDataSource(getApplicationContext(), myUri);
//        mediaPlayer.prepare();
//        mediaPlayer.start();
        
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnErrorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand() " + action);

            if (ACTION_PLAY.equals(action)) {
                
            } else if (ACTION_PAUSE.equals(action)) {
                
            } else if (ACTION_NEXT_SONG.equals(action)) {
                
            } else if (ACTION_PREV_SONG.equals(action)) {
                
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    
    public int play() {
        return 0;
    }
    
    public int pause() {
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }
}
