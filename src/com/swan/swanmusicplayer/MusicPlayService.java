package com.swan.swanmusicplayer;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayService extends Service implements OnErrorListener, OnCompletionListener {
    private static final String TAG = "MusicPlayService";

    public static final String ACTION_PREV = "com.swan.swanmusicplayer.action.PREV";
    public static final String ACTION_NEXT = "com.swan.swanmusicplayer.action.NEXT";
    public static final String ACTION_PLAY = "com.swan.swanmusicplayer.action.PLAY";
    public static final String ACTION_PAUSE = "com.swan.swanmusicplayer.action.PAUSE";
    public static final String ACTION_PLAY_TOGGLE = "com.swan.swanmusicplayer.action.PLAY_TOGGLE";

    private MediaPlayer mPlayer;
    private MusicListAdapter mMusicListAdapter;
    private int mCurrentPosition;
    private MusicPlayReceiver mMusicPlayReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        // Uri myUri = ....; // initialize Uri here
        // MediaPlayer mediaPlayer = new MediaPlayer();
        // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // mediaPlayer.setDataSource(getApplicationContext(), myUri);
        // mediaPlayer.prepare();
        // mediaPlayer.start();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);

        // Broadcast Receiver
        mMusicPlayReceiver = new MusicPlayReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mMusicPlayReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mMusicListAdapter = MusicListActivity.getMusicListAdapter();

            String action = intent.getAction();
            Log.d(TAG, "onStartCommand() " + action);

            if (ACTION_PREV.equals(action)) {
                if (mCurrentPosition > 0) {
                    mCurrentPosition--;
                } else {
                    mCurrentPosition = mMusicListAdapter.getCount() - 1;                    
                }
                play(mCurrentPosition);
            } else if (ACTION_NEXT.equals(action)) {
                if (mCurrentPosition < mMusicListAdapter.getCount() - 1) {
                    mCurrentPosition++;
                } else {
                    mCurrentPosition = 0;                    
                }
                play(mCurrentPosition);
            } else if (ACTION_PLAY.equals(action)) {
                int position = intent.getIntExtra("position", -1);
                
                if (position != -1) {
                    play(position);
                }
            } else if (ACTION_PAUSE.equals(action)) {
                pause();
            } else if (ACTION_PLAY_TOGGLE.equals(action)) {
                playToggle();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        mMusicListAdapter = null; // for GC
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(int position) {
        mCurrentPosition = position;

        if (mMusicListAdapter == null) {
            Log.d(TAG, "play(): MediaPlayer is null");
            return;
        }
        
        Music music = (Music) mMusicListAdapter.getItem(mCurrentPosition);
        if (music == null) {
            Log.d(TAG, "play() : No music");
            return;
        }
        
        if (mPlayer == null) {
            Log.d(TAG, "play(): MediaPlayer is null");
            return;
        }
        
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getData());
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlayer.start();
        } catch (Exception e) {
            Log.d(TAG, "play error");
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mPlayer == null) {
            Log.d(TAG, "pause(): MediaPlayer is null");
            return;
        }
        mPlayer.pause();
    }
    
    public void playToggle() {
        if (mPlayer == null) {
            Log.d(TAG, "playToggle(): MediaPlayer is null");
            return;
        }
        
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Broadcast Receiver for HEADSET
     * @author Suhwan Hwang
     *
     */
    private class MusicPlayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            // Headset unplugged 
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.getIntExtra("state", 0) == 0
                        && mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
            }
        }
        
    }
}
