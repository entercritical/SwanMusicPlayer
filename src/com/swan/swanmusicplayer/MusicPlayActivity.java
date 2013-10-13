package com.swan.swanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    private SeekBar mPlayerSeekBar;
    private MusicListAdapter mMusicListAdapter;
    private Music mMusic;
    private int mPosition;
    private boolean mIsPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", -1);
        
        if (mPosition == -1) {
            Log.d(TAG, "Wrong Play position");
            finish();
            return;
        }
        
        // Get Music Data
        mMusicListAdapter = MusicListActivity.getMusicListAdapter();
        mMusic = (Music)mMusicListAdapter.getItem(mPosition);
        Log.d(TAG, "Play Music: " + mMusic);
        
        // initialize views
        initViews();
        
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
                prev();
            }
        });
        mNext = (ImageButton)findViewById(R.id.nextButton);
        mNext.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                next();
            }
        });
        mPlay = (ImageButton)findViewById(R.id.playButton);
        mPlay.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                playToggle();
            }
        });

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
    
    /**
     * play music
     */
    private void play() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY);
        intent.putExtra("position", mPosition);
        startService(intent);
        
        mIsPlaying = true;
        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
    }
    
    /**
     * pause music
     */
    private void pause() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PAUSE);
        intent.putExtra("position", mPosition);
        startService(intent);
        
        mIsPlaying = false;
        mPlay.setImageResource(R.drawable.av_play);     // change play button image
    }
    
    /**
     * play or pause by playing status
     */
    private void playToggle() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PLAY_TOGGLE);
        intent.putExtra("position", mPosition);
        startService(intent);
        
        mPlay.setImageResource(mIsPlaying ? R.drawable.av_play : R.drawable.av_pause);    // change play button image
        mIsPlaying = !mIsPlaying;
    }
    
    /**
     * play previous music
     */
    private void prev() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_PREV);
        startService(intent);
        
        if (mPosition > 0) {
            mPosition--;
        } else {
            mPosition = mMusicListAdapter.getCount();
        }
        mMusic = (Music)mMusicListAdapter.getItem(mPosition);
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

        mIsPlaying = true;
        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
    }
    
    /**
     * play next music
     */
    private void next() {
        Intent intent = new Intent(getBaseContext(), MusicPlayService.class);
        intent.setAction(MusicPlayService.ACTION_NEXT);
        startService(intent);
        
        if (mPosition < mMusicListAdapter.getCount() - 1) {
            mPosition++;
        } else {
            mPosition = 0;
        }
        mMusic = (Music)mMusicListAdapter.getItem(mPosition);
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
        
        mIsPlaying = true;
        mPlay.setImageResource(R.drawable.av_pause);    // change play button image
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        mMusicListAdapter = null;   // for GC
    }
}
