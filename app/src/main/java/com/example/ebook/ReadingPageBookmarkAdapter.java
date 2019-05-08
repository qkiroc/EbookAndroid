package com.example.ebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ReadingPageBookmarkAdapter extends BaseAdapter {
    private LinkedList<ReadingPageBookmarkList> mData;
    private Context mContext;
    public ReadingPageBookmarkAdapter(LinkedList<ReadingPageBookmarkList> mData, Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_readpagebookmark,parent,false);
        TextView title = convertView.findViewById(R.id.listitem_bookmarktitle);
        TextView page = convertView.findViewById(R.id.listitem_bookmarkpage);
        TextView time = convertView.findViewById(R.id.listitem_bookmarktime);
        TextView textView = convertView.findViewById(R.id.listitem_bookmarktext);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(new Long(mData.get(position).getTime()));
        time.setText(simpleDateFormat.format(date));
        title.setText(mData.get(position).getTitle());
        page.setText(String.valueOf(Integer.valueOf(mData.get(position).getPage())+1));
        textView.setText(mData.get(position).getText());
        return convertView;
    }
}
