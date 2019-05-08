package com.example.ebook;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ebook.R;

import java.util.LinkedList;

public class SearchBookResultAdapter extends BaseAdapter {

    private LinkedList<SearchBookResultList> mData;
    private Context mContext;

    public SearchBookResultAdapter(LinkedList<SearchBookResultList> mData, Context mContext){
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_searchbookresult,parent,false);
        TextView fileName = convertView.findViewById(R.id.listitem_searchbookresulttext);
        ImageView checkBox = convertView.findViewById(R.id.listitem_searchbookresultchoose);
        TextView path = convertView.findViewById(R.id.listitem_searchbookresultpath);
        TextView ischeck = convertView.findViewById(R.id.listitem_searchbookresultischeck);
        TextView hasadd = convertView.findViewById(R.id.listitem_searchbookresulthasadd);
        RelativeLayout bg = convertView.findViewById(R.id.listitem_searchbookresultbg);
        String nowpath = mData.get(position).getPath();
        Boolean isAdd = mData.get(position).getIsChecked();
        Boolean isfile = mData.get(position).getIsfile();
        if(isfile){
            if(isAdd) {
                checkBox.setVisibility(View.GONE);
                hasadd.setVisibility(View.VISIBLE);
            }
            else {
                checkBox.setVisibility(View.VISIBLE);
                hasadd.setVisibility(View.GONE);
            }
            if(mData.get(position).getCheckSet().contains(nowpath)) {
                ischeck.setText("1");
                checkBox.setSelected(true);
            }
            else {
                ischeck.setText("0");
            }
            bg.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else {
            ischeck.setText("2");
            checkBox.setVisibility(View.GONE);
            hasadd.setVisibility(View.GONE);
        }
        path.setText(nowpath);
        fileName.setText(mData.get(position).getFileName());
        return convertView;
    }
}
