package com.example.eiditortext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eiditortext.MyAdapter.ViewHolder;
import com.example.cache.ImageLoader;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private static final String TAG = "LoaderAdapter";
    private boolean mBusy = false;

    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
    }

    
    private ImageLoader mImageLoader;
    private int mCount;
    private Context mContext;
    private ArrayList<NewsRSS.News> NewsArrays;
    
    
    public MyAdapter(int count, Context context, ArrayList<NewsRSS.News> newsdata) {
        this.mCount = count;
        this.mContext = context;
        NewsArrays = newsdata;
        mImageLoader = new ImageLoader(context);
    }
    
    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView_title = (TextView) convertView
                    .findViewById(R.id.newstitle);
            viewHolder.mTextView_des = (TextView) convertView
                    .findViewById(R.id.newsdes);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.newsimage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = "";
        String title = "";
        String des = "";
        
        if(NewsArrays.size() <= 0)return convertView;
        
        Log.v("zswww","posi=="+position);
        
        url = NewsArrays.get(position % NewsArrays.size()).newsurl;
        title = NewsArrays.get(position % NewsArrays.size()).title;
        des = NewsArrays.get(position % NewsArrays.size()).description;
        
        //Log.v("zswww", "url=="+url);
        
        viewHolder.mImageView.setImageResource(R.drawable.ic_launcher);
        

        if(url != ""){
            viewHolder.mImageView.setVisibility(View.VISIBLE);
            mImageLoader.DisplayImage(url, viewHolder.mImageView, false);       
        }
        else{
            Log.v("zswww", "url is null");
            viewHolder.mImageView.setVisibility(View.GONE);
        }
        viewHolder.mTextView_title.setText(title);
        viewHolder.mTextView_des.setText(des);
        
        return convertView;
    }

    static class ViewHolder {
        TextView mTextView_title;
        TextView mTextView_des;
        ImageView mImageView;
    }

}
