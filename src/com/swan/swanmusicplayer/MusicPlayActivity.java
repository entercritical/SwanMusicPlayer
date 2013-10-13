package com.swan.swanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
        
    }
    
    /**
     * initialize views
     */
    private void initViews() {
        setContentView(R.layout.music_player);
        mAlbumImage = (ImageView)findViewById(R.id.albumImage);
        mPrev = (ImageButton)findViewById(R.id.prevButton);
        mNext = (ImageButton)findViewById(R.id.nextButton);
        mPlay = (ImageButton)findViewById(R.id.playButton);

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
        
        mMusicListAdapter = null;   // for GC
    }
}
