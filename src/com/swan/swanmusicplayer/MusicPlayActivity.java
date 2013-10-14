package com.swan.swanmusicplayer;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MusicPlayActivity extends Activity {
    private static final String TAG = "MusicPlayActivity";

    private static final int UPDATE_PLAYTIME = 2;

    private ImageView mAlbumImage;
    private ImageButton mPrev, mPlay, mNext;
    private SeekBar mSeekBar;
    private TextView mPlayTime;
    private Music mMusic;
    private int mMusicIndex;
    private PlayStateReceiver mPlayStateReceiver;
    private Timer mPlayTimer; 
    private UpdateHandler mHandler = new UpdateHandler(this);   

    /**
     * PlayStateReceiver
     * 
     * Broadcast Receiver for Player state
     * 
     * @author Suhwan Hwang
     *
     */
    private class PlayStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive() : " + action);
            
            if (MusicPlayService.ACTION_BROADCAST_PREPARED.equals(action)) {
                // get onPrepared() & set duration of SeekBar
                int duration = intent.getExtras().getInt("duration", 0);
                if (mSeekBar != null) {
                    mSeekBar.setMax(duration);
                }
                
                // set play timer for update time & seekbar
                setPlayTimer(1000);
            } else if (MusicPlayService.ACTION_BROADCAST_COMPLETE.equals(action)) {
                // play next music
                playNext();            
            } else if (MusicPlayService.ACTION_BROADCAST_PAUSE.equals(action)) {
                // change play button image
                if (mPlay != null)
                    mPlay.setImageResource(R.drawable.av_play);
            }
        }
    }
    
    /**
     * PlayTimerTask
     * 
     * TimerTask for SeekBar & Play time text update
     * 
     * @author Suhwan Hwang
     *
     */
    private class PlayTimerTask extends TimerTask {
        
        @Override
        public void run() {
            int position = MusicPlayService.getInstance(getBaseContext()).getCurrentPlayPosition();
            
            //Log.d(TAG, "PlayTimerTask " + time);
            updatePlayTime(position);
        }
    };
    
    /**
     * UpdateHandler
     * 
     * @author Suhwan Hwang
     *
     */
    private static class UpdateHandler extends Handler {
        WeakReference<MusicPlayActivity> mRef;

        UpdateHandler(MusicPlayActivity act) {
            mRef = new WeakReference<MusicPlayActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicPlayActivity v = mRef.get();
            switch(msg.what) {

            case UPDATE_PLAYTIME:
                v.setPlayTime((Integer)msg.obj);
                break;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mMusicIndex = intent.getIntExtra("music_index", -1);
        
        if (mMusicIndex == -1) {
            Log.d(TAG, "Wrong Play Index");
            finish();
            return;
        }
        
        // Get Music Data
        MusicList.getInstance().refreshAllMusic(this);
        mMusic = MusicList.getInstance().getMusicList().get(mMusicIndex);
        Log.d(TAG, "Play Music: " + mMusic);
        
        // initialize views
        initViews();
        
        // Broadcast Receiver for play service
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.ACTION_BROADCAST_PREPARED);
        filter.addAction(MusicPlayService.ACTION_BROADCAST_PAUSE);
        filter.addAction(MusicPlayService.ACTION_BROADCAST_COMPLETE);
        mPlayStateReceiver = new PlayStateReceiver();
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mPlayStateReceiver, filter);
        
        // play music
        play();
    }
    
    /**
     * initialize views
     */
    private void initViews() {
        setContentView(R.layout.music_player);
        
        mAlbumImage = (ImageView)findViewById(R.id.albumImage);
        mPrev = (ImageButton)findViewById(R.id.prevButton);
        mPrev.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        mNext = (ImageButton)findViewById(R.id.nextButton);
        mNext.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        mPlay = (ImageButton)findViewById(R.id.playButton);
        mPlay.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                playToggle();
            }
        });
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // seek new positon 
                seek(seekBar.getProgress());
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                // move seek bar
                if (fromUser == true) {
                    setPlayTime(seekBar.getProgress());
                }
            }
        });
        
        mPlayTime = (TextView)findViewById(R.id.playTime);

        updateDisplay();
    }
    
    /**
     * set PlayTimer
     * 
     * @param msec period
     */
    private void setPlayTimer(int msec) {
        if (mPlayTimer != null) {
            mPlayTimer.cancel();
        }
        
        mPlayTimer = new Timer();
        mPlayTimer.schedule(new PlayTimerTask(), 0, msec);
    }
    
    /**
     * stop PlayTimer
     */
    private void stopPlayTimer() {
        if (mPlayTimer != null) {
            mPlayTimer.cancel();
            mPlayTimer = null;
        }
    }
    
    /**
     * update play time for handler
     * 
     * @param time play time
     */
    private void updatePlayTime(int time) {
        Message m = new Message();
        m.what = UPDATE_PLAYTIME;
        m.obj = Integer.valueOf(time);
        
        mHandler.sendMessage(m);        
    }
    
    /**
     * set play time & seek bar
     * 
     * @param t play time
     */
    private void setPlayTime(int t) {
        int sec = t / 1000;
        int h, m, s, tmp;

        if (sec < 3600) {
            h = 0;
            m = sec / 60;
            s = sec % 60;
        } else {
            h = sec / 3600;
            tmp = sec % 3600;
            m = tmp / 60;
            s = tmp % 60;
        }
        
        if (mPlayTime != null) {
            mPlayTime.setText(String.format("%02d:%02d", m, s));
        }
        
        if (mSeekBar != null) {
            mSeekBar.setProgress(t);
        }
    }

    
    /**
     * play music
     */
    private void play() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY);
        intent.putExtra("music_index", mMusicIndex);
        startService(intent);
        
        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
    }
    
    /**
     * pause music
     */
    private void pause() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PAUSE);
        intent.putExtra("music_index", mMusicIndex);
        startService(intent);
        
        mPlay.setImageResource(R.drawable.av_play);     // change play button image
    }
    
    /**
     * play or pause by playing status
     */
    private void playToggle() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY_TOGGLE);
        intent.putExtra("music_index", mMusicIndex);
        startService(intent);
        
        mPlay.setImageResource(MusicPlayService.getInstance(this).isPlaying() ? R.drawable.av_play : R.drawable.av_pause);    // change play button image
    }
    
    /**
     * play previous music
     */
    private void playPrev() {        
        if (MusicPlayService.getInstance(this).getCurrentPlayPosition() < 2000) {
            if (mMusicIndex > 0) {
                mMusicIndex--;
            } else {
                mMusicIndex = MusicList.getInstance().getMusicList().size();
            }
            updateDisplay();
        }               

        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
        
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY);
        intent.putExtra("music_index", mMusicIndex);
        startService(intent);
    }
    
    /**
     * play next music
     */
    private void playNext() {        
        if (mMusicIndex < MusicList.getInstance().getMusicList().size() - 1) {
            mMusicIndex++;
        } else {
            mMusicIndex = 0;
        }
        updateDisplay();            
        
        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
        
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY);
        intent.putExtra("music_index", mMusicIndex);
        startService(intent);
    }
    
    /**
     * seek positon
     * 
     * @param position
     */
    private void seek(int position) {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_SEEK);
        intent.putExtra("position", position);
        startService(intent);
    }
    
    /**
     * update display (Title, Album art)
     */
    private void updateDisplay() {
        mMusic = (Music)MusicList.getInstance().getMusicList().get(mMusicIndex);
        if (mMusic != null) {
            // Set album art image
            Bitmap albumArt = mMusic.getAlbumArt(this);
            if (albumArt != null) {
                mAlbumImage.setImageBitmap(albumArt);
            } else {
                mAlbumImage.setImageResource(R.drawable.music);
            }
            
            // Set Title
            setTitle(mMusic.getTitle());
        }         
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                
        // stop play timer
        stopPlayTimer();
        
        //unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPlayStateReceiver);
    }    
}
