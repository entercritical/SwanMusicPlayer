package com.swan.swanmusicplayer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter {
    private static final String TAG = "MusicListAdapter";
    
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Music> mMusicList = new ArrayList<Music>();
    
    public static final String[] PROJECTION = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
    };
    
    public MusicListAdapter(Context context) {
        mContext = context;
        
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        ContentResolver resolver = mContext.getContentResolver();
        
        Uri media = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = resolver.query(media, PROJECTION, selection, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "Music List Empty");
            return;
        }

        int count = cursor.getCount();

        for (int i = 0; i != count; ++i) {
            if (!cursor.moveToNext()) {
                break;
            }
            Log.d(TAG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mMusicList.add(new Music(
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    ));

        }
        cursor.close();

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
    
    private static class ViewHolder {
        public long id;
        //public ImageView album;
        public TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.music_list_row, null);
            holder = new ViewHolder();
            //holder.album = (ImageView)convertView.findViewById(R.id.musicListImage);
            holder.text = (TextView)convertView.findViewById(R.id.musicListText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        Music music = mMusicList.get(position);        
        String line1 = music.getTitle();
        String line2 = music.getArtist();
        SpannableStringBuilder sb = new SpannableStringBuilder(line1);
        sb.append('\n');
        sb.append(line2);
        sb.setSpan(new ForegroundColorSpan(Color.GRAY), line1.length() + 1, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.text.setText(sb);
        holder.id = music.getId();
        
//        Bitmap bitmap = music.getCover(mContext);
//        if (bitmap != null)
//            holder.album.setImageBitmap(music.getCover(mContext));
        return convertView;
    }

    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }
}
