package com.swan.swanmusicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MusicPlayActivity extends Activity {
    private static final String TAG = "MusicPlayActivity";
    private ImageView mAlbumImage;
    private ImageButton mPrev, mPlay, mNext;
    private Music mMusic;
    private int mMusicIndex;
    private PlayStateReceiver mPlayStateReceiver;

    private class PlayStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive() : " + action);
            
            if (MusicPlayService.ACTION_BROADCAST_COMPLETE.equals(action)) {
                // play next music
                playNext();            
            } else if (MusicPlayService.ACTION_BROADCAST_PAUSE.equals(action)) {
                // change play button image
                if (mPlay != null)
                    mPlay.setImageResource(R.drawable.av_pause);
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
        mMusic = MusicList.getInstance().getMusicList().get(mMusicIndex);
        Log.d(TAG, "Play Music: " + mMusic);
        
        // initialize views
        initViews();
        
        // Broadcast Receiver for play service
        IntentFilter filter = new IntentFilter();
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

        updateDisplay();
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
                
        //unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPlayStateReceiver);
    }    
}
