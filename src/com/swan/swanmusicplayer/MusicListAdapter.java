package com.swan.swanmusicplayer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * MucisListAdapter class
 * 
 * @author Suhwan Hwang
 *
 */
public class MusicListAdapter extends BaseAdapter {
    private static final String TAG = "MusicListAdapter";
    
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Music> mMusicList;   // music list  
    
    /**
     * Constructor
     * 
     * @param context context
     * @param musicList music list
     */
    public MusicListAdapter(Context context, ArrayList<Music> musicList) {
        mContext = context;       
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMusicList = musicList;
    }
    
    @Override
    public int getCount() {
        if (mMusicList == null)
            return 0;
        
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mMusicList == null)
            return null;
        
        return mMusicList.get(position); 
    }

    @Override
    public long getItemId(int position) {
        return mMusicList.get(position).getId();
    }
    
    /**
     * ViewHolder class for performance
     * 
     * @author Suhwan Hwang
     *
     */
    private static class ViewHolder {
        public long id;
        public TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.music_list_row, null);
            holder = new ViewHolder();
            holder.text = (TextView)convertView.findViewById(R.id.musicListText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        // Get Music data
        Music music = mMusicList.get(position);
        
        // Make 2 line String (Title, Artist)
        String line1 = music.getTitle();
        String line2 = music.getArtist();
        SpannableStringBuilder sb = new SpannableStringBuilder(line1);
        sb.append('\n');
        sb.append(line2);
        sb.setSpan(new ForegroundColorSpan(Color.GRAY), line1.length() + 1, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.text.setText(sb);
        holder.id = music.getId();
        
        return convertView;
    }

    /**
     * get Music List
     * @return  Music List
     */
    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }
}
