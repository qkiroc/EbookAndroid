package com.example.ebook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ebook.Util.Picture;
import com.example.ebook.Util.Util;

import java.util.LinkedList;

public class BookrackAdapter extends BaseAdapter {
    private LinkedList<BookrackList> mData;
    private Context mContext;
    public Boolean flag = false;

    public BookrackAdapter(LinkedList<BookrackList> mData, Context mContext){
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_bookrack,parent,false);
        TextView title = convertView.findViewById(R.id.listitem_bookracktitle);
        TextView path = convertView.findViewById(R.id.listitem_bookrackpath);
        TextView titletop = convertView.findViewById(R.id.listitem_bookracktitletop);
        ImageView bg = convertView.findViewById(R.id.listitem_bookrackpicture);
        ImageView ischeck = convertView.findViewById(R.id.img_bookrackdelete);
        if(flag){
            ischeck.setVisibility(View.VISIBLE);
        }
        else {
            ischeck.setVisibility(View.GONE);
        }
        if(mData.get(position).getIscheck()){
            ischeck.setSelected(true);
        }
        else {
            ischeck.setSelected(false);
        }
        String cover = mData.get(position).getCover();
        if (cover.length() > 0 ){
            bg.setImageBitmap(Picture.getLoacalBitmap(cover));
            titletop.setVisibility(View.GONE);
        }
        title.setText(mData.get(position).getTitle());
        titletop.setText(mData.get(position).getTitle());
        path.setText(mData.get(position).getPath());
        return convertView;
    }
}
