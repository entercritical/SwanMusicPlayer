package com.swan.swanmusicplayer;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class MusicListActivity extends ListActivity {
    private static final String TAG = "MainActivity";
    private MusicUtil mMusicUtil;
    private ListView mMusicListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        mMusicListView = getListView();
        
        setListAdapter(new MusicListAdapter(this));
        
        //mMusicListView = (ListView)findViewById(R.id.musicListView);
        
        //mMusicUtil = MusicUtil.getInstance(this);
        //mMusicUtil.resolveMusicList();
        
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
