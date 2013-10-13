package com.swan.swanmusicplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MusicListActivity extends ListActivity implements OnItemClickListener {
    private static final String TAG = "MainActivity";
    private ListView mMusicListView;
    private static MusicListAdapter mMusicListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retrieve Music Data 
        mMusicListAdapter = new MusicListAdapter(this);
        setListAdapter(mMusicListAdapter);
        
        // Set Item Click Listener
        mMusicListView = getListView();        
        mMusicListView.setOnItemClickListener(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        mMusicListAdapter = null;   // for GC
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Log.d(TAG, "onItemClick(): MusicList " + position);
        
        // Move to MusicPlayer
        Intent intent = new Intent(this, MusicPlayActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public static MusicListAdapter getMusicListAdapter() {
        return mMusicListAdapter;
    }
}
