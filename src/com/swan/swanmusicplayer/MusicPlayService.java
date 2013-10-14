package com.swan.swanmusicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MusicPlayService extends Service implements OnErrorListener, OnCompletionListener {
    private static final String TAG = "MusicPlayService";

    public static final String ACTION_PLAY = "com.swan.swanmusicplayer.action.PLAY";
    public static final String ACTION_PAUSE = "com.swan.swanmusicplayer.action.PAUSE";
    public static final String ACTION_PLAY_TOGGLE = "com.swan.swanmusicplayer.action.PLAY_TOGGLE";

    public static final String ACTION_BROADCAST_COMPLETE = "com.swan.swanmusicplayer.action.BROADCAST_COMPLETE";
    public static final String ACTION_BROADCAST_PAUSE = "com.swan.swanmusicplayer.action.BROADCAST_PAUSE";
    
    private MediaPlayer mPlayer;
    private int mCurrentPosition;
    private MusicPlayReceiver mMusicPlayReceiver;
    private int mInitHeadSetState = -1;

    // for synchronize service instance
    private static final Object[] sWait = new Object[0];
    private static MusicPlayService sInstance;
    
    /**
     * return the MusicPlayService instance
     */
    public static MusicPlayService getInstance(Context context) {
        if (sInstance == null) {
            context.startService(new Intent(context, MusicPlayService.class));

            while (sInstance == null) {
                try {
                    synchronized (sWait) {
                        sWait.wait();
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }

        return sInstance;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        // create media player
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);

        // register Broadcast Receiver
        mMusicPlayReceiver = new MusicPlayReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mMusicPlayReceiver, filter);
        
        // initialize service instance
        sInstance = this;
        synchronized (sWait) {
            sWait.notifyAll();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand() " + action);

            if (ACTION_PLAY.equals(action)) {    // Play music
                int position = intent.getIntExtra("position", -1);
                
                if (position != -1) {
                    play(position);
                }
            } else if (ACTION_PAUSE.equals(action)) {   // Pause music
                pause();
            } else if (ACTION_PLAY_TOGGLE.equals(action)) { // Play or Pause
                playToggle();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        unregisterReceiver(mMusicPlayReceiver);
    }

    /**
     * Play music
     * 
     * @param position position of music list
     */
    public void play(int position) {
        mCurrentPosition = position;
        
        Music music = (Music) MusicList.getInstance().getMusicList().get(mCurrentPosition);
        if (music == null) {
            Log.e(TAG, "play() : No music or invalid position");
            return;
        }
        
        if (mPlayer == null) {
            Log.e(TAG, "play(): MediaPlayer is null");
            return;
        }
        Log.d(TAG, "play() : " + music.getTitle());
        
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getData());
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "play(): error");
            e.printStackTrace();
        }
    }

    /**
     * Pause music
     */
    public void pause() {
        if (mPlayer == null) {
            Log.e(TAG, "pause(): MediaPlayer is null");
            return;
        }
        mPlayer.pause();
    }
    
    /**
     * Play or Pause music
     */
    public void playToggle() {
        if (mPlayer == null) {
            Log.e(TAG, "playToggle(): MediaPlayer is null");
            return;
        }
        
        if (mPlayer.isPlaying()) {  // Is Playing?
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }
    
    /**
     * return current play position
     * 
     * @return current position
     */
    public int getCurrentPlayPosition() {
        if (mPlayer == null) {
            Log.e(TAG, "getCurrentTime(): MediaPlayer is null");
            return 0;
        }
        
        return mPlayer.getCurrentPosition();
    }
    
    /**
     * is playing music
     * 
     * @return true/false
     */
    public boolean isPlaying() {
        if (mPlayer == null) {
            Log.e(TAG, "isPlaying(): MediaPlayer is null");
            return false;
        }
        
        return mPlayer.isPlaying();
            
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "MediaPlayer error : " + what + ' ' + extra);
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Broadcast to activity for play next music
        Intent localIntent = new Intent(ACTION_BROADCAST_COMPLETE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
    }
    
    /**
     * Broadcast Receiver for Headset unplug 
     * 
     * @author Suhwan Hwang
     *
     */
    private class MusicPlayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            // Headset unplugged 
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                Log.d(TAG, "onReceive() : " + action + " " + intent.getIntExtra("state", 0));
                
                if (mInitHeadSetState == -1) {  // initial state
                    mInitHeadSetState = intent.getIntExtra("state", 0);
                    return;
                }
                
                if (intent.getIntExtra("state", 0) == 0
                        && mPlayer != null && mPlayer.isPlaying()) {
                    // Music stop
                    mPlayer.pause();
                    
                    // Broadcast to activity for update display
                    Intent localIntent = new Intent(ACTION_BROADCAST_PAUSE);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                }
            }
        }
    }
}
