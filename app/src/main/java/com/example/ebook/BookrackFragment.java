package com.example.ebook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BookrackFragment extends Fragment {

    private SQLiteDatabase db;
    private List<String> path;
    private List<String> titles;
    private List<String> covers;
    private GridView content;
    private BookrackAdapter adapter;
    private LinkedList<BookrackList> mData;
    private RelativeLayout deletetop;
    private TextView title;
    private int count = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.bookrack_content, container,false);
        content = contentView.findViewById(R.id.gv_bookrackgv);
        deletetop = contentView.findViewById(R.id.rl_bookrackdelete);
        title = contentView.findViewById(R.id.tv_bookracktitle);
        content.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.flag){
                    for (BookrackList list : mData){
                        list.ischeck = false;
                    }
                }
                else {
                    count = 1;
                    mData.get(position).ischeck = true;
                    title.setText("已选中"+count+"本图书");
                }
                adapter.flag = !adapter.flag;
                adapter.notifyDataSetChanged();
                isDeletelan();
                return true;
            }
        });
        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!adapter.flag){
                    TextView path = (TextView)view.findViewById(R.id.listitem_bookrackpath);
                    String pathstr = (String) path.getText();
                    Intent intent = new Intent(getActivity(), ReadingPageActivity.class);
                    intent.putExtra("path", pathstr);
                    startActivity(intent);
                }
                else {
                    if(mData.get(position).ischeck){
                        mData.get(position).ischeck = false;
                        count--;
                    }
                    else {
                        mData.get(position).ischeck = true;
                        count++;
                    }
                    title.setText("已选中"+count+"本图书");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        TextView concel = contentView.findViewById(R.id.tv_bookrackconcel);
        concel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BookrackList list : mData){
                    list.ischeck = false;
                }
                adapter.flag = !adapter.flag;
                adapter.notifyDataSetChanged();
                isDeletelan();
            }
        });
        TextView delete = contentView.findViewById(R.id.tv_bookrackdelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < mData.size(); i++){
                    if(mData.get(i).ischeck){
                        String path = mData.get(i).getPath();
                        db.execSQL("delete from booktable where path='"+path+"'");
                        mData.remove(i);
                        i--;
                    }
                }
                adapter.flag = false;
                adapter.notifyDataSetChanged();
                isDeletelan();
            }
        });

        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        path = new ArrayList<String>();
        titles = new ArrayList<String>();
        covers = new ArrayList<String>();
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir()+"/ebook.db",null);
        Cursor cursor = db.rawQuery("select * from booktable", null);
        while (cursor.moveToNext()) {
            path.add(cursor.getString(1));
            titles.add(cursor.getString(2));
            covers.add(cursor.getString(3));
        }

        mData = new LinkedList<BookrackList>();
        for (int i = 0; i < path.size(); i++){
            Log.d("HEHE", covers.get(i));
            mData.add(new BookrackList(titles.get(i), path.get(i), covers.get(i), false));
        }
        adapter = new BookrackAdapter(mData, getActivity());
        content.setAdapter(adapter);
        isDeletelan();
    }
    public void isDeletelan() {
        if(adapter.flag){
            final TranslateAnimation bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, -1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            deletetop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    deletetop.setVisibility(View.VISIBLE);
                    deletetop.startAnimation(bottomAnimation);
                }
            }, 0);
        }
        else {
            final TranslateAnimation  bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, -1);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            deletetop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    deletetop.setVisibility(View.GONE);
                    deletetop.startAnimation(bottomAnimation);
                }
            }, 0);
        }
    }
}
