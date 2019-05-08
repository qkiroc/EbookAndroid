package com.example.ebook;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SearchBookResultActivity<OnResume> extends AppCompatActivity implements View.OnClickListener{

    private static Set<String> file_txt_path;
//    private static String onefilename;
//    private static String onefiletxtpath;
    private ProgressDialog mProgressDialog;
    private ListView searchbookresult;
    private Context mContext;
    private Handler mHandle;
    private Set<String> checkedSet = new HashSet<String>();
    private LinkedList<SearchBookResultList> mData = new LinkedList<SearchBookResultList>();
    private SQLiteDatabase db;
    private Set<String> hasadd = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.initWindow(getWindow());

        setContentView(R.layout.activity_search_book_result);


        searchbookresult =(ListView) findViewById(R.id.lv_searchbookresult);
        mContext = SearchBookResultActivity.this;

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在搜索书籍，请稍候 ……");
        mProgressDialog.show();

        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        Cursor cursor = db.rawQuery("select * from booktable", null);
        while (cursor.moveToNext()) {
            hasadd.add(cursor.getString(1));
        }

        mHandle = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what){
                    case 13:
                        String[] data1 = String.valueOf(message.obj).split("\n");
                        addBookToList(data1[0],data1[1],data1[2]);
                        break;
                    case 12:
                        String[] data = String.valueOf(message.obj).split("\n");
                        addBookToList(data[0],data[1],"1");
                        break;
                    case 11:
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            return;
                        }
                        break;
                }
            }
        };


        file_txt_path = new HashSet<String>();
        new Thread() {
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory().toString());
                LoadFile(file);
                mHandle.sendEmptyMessage(11);
            }
        }.start();

        searchbookresult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView choose = (ImageView) view.findViewById(R.id.listitem_searchbookresultchoose);
                TextView ischeck = (TextView) view.findViewById(R.id.listitem_searchbookresultischeck);
                TextView path = (TextView) view.findViewById(R.id.listitem_searchbookresultpath);
                if((String) ischeck.getText() == "0") {
                    if(!hasadd.contains((String) path.getText())){
                        choose.setSelected(true);
                        checkedSet.add((String) path.getText());
                        ischeck.setText("1");
                    }
                }
                else if((String) ischeck.getText() == "1") {
                    checkedSet.remove((String) path.getText());
                    choose.setSelected(false);
                    ischeck.setText("0");
                }
            }
        });

        ImageView goback = findViewById(R.id.img_seacrhbookresultactive_goback);
        goback.setOnClickListener(this);

        TextView addtobookrack = findViewById(R.id.tv_addtobookrack);
        addtobookrack.setOnClickListener(this);

    }
    private String filepath;
    private void LoadFile(File file) {
        File[] files = file.listFiles();
        try {
            for (File f : files) {
                if (!f.isDirectory()) {
                    if (f.getName().endsWith(".txt")) {
                        long size = f.length();
                        if (size > 400 * 1024) {
                            String pathname = f.getPath();
                            String[] paths = pathname.split("/");
                            int length = paths.length;
                            if (!paths[length-2].equals(filepath)){
                                filepath = paths[length-2];
                                Message msg = new Message();
                                msg.what = 13;
                                msg.obj = f.getName()+"\n"+paths[length-2]+"\n"+"0";
                                mHandle.sendMessage(msg);
                            }
                            Message msg = new Message();
                            msg.what = 12;
                            msg.obj = f.getName()+"\n"+pathname;
                            mHandle.sendMessage(msg);
                        }
                    }
                } else if (f.isDirectory()) {
                    LoadFile(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBookToList(String onefilename, String onefiletxtpath, String isdirectory) {
        if(!file_txt_path.contains(onefiletxtpath)){
            Boolean isChecked = false;
            if (hasadd.contains(onefiletxtpath)) {
                isChecked = true;
            }
            if (isdirectory.equals("0")){
                mData.add(new SearchBookResultList(onefiletxtpath, onefiletxtpath, checkedSet, isChecked, false));
            }
            else {
                mData.add(new SearchBookResultList(onefilename, onefiletxtpath, checkedSet, isChecked, true));
            }
            SearchBookResultAdapter adapter = new SearchBookResultAdapter(mData, mContext);
            searchbookresult.setAdapter(adapter);
            file_txt_path.add(onefiletxtpath);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_seacrhbookresultactive_goback:
                break;
            case R.id.tv_addtobookrack:
                for(String path: checkedSet) {
                    ContentValues cValue = new ContentValues();
                    cValue.put("path", path);
                    int t = path.split("/").length;
                    String title = path.split("/")[t-1];
                    cValue.put("title",title.substring(0, title.length()-4));
                    cValue.put("cover","");
                    db.insert("booktable",null, cValue);
                }
                break;
        }
        finish();
    }
}
