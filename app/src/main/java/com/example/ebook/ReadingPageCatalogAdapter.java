package com.example.ebook;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ReadingPageCatalogAdapter extends BaseAdapter {
    private LinkedList<ReadingPageCatalogList> mData;
    private Context mContext;
    public ReadingPageCatalogAdapter(LinkedList<ReadingPageCatalogList> mData, Context mContext){
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_readpagecatalog,parent,false);
        TextView title = convertView.findViewById(R.id.listitem_catalogtitle);
        TextView pagenumber = convertView.findViewById(R.id.listitem_catalogpagenumber);
        Boolean isnowsection = mData.get(position).getIsnowsection();
        title.setText(mData.get(position).getTitle());
        if(isnowsection){
            title.setTextColor(Color.parseColor("#FF9800"));
        }
        pagenumber.setText(String.valueOf(mData.get(position).getPagenumber()));
        return convertView;
    }
}
