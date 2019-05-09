package com.example.ebook;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.ebook.Util.Util;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txt_home;
    private TextView txt_bookrack;
    private TextView txt_zone;
    private TextView txt_mine;
    private FrameLayout ly_content;

    private int flag;

    private HomeFragment homeFragment;
    private ZoneFragment zoneFragment;
    private MineFragment mineFragment;
    private BookrackFragment bookrackFragment;
    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Util.initWindow(getWindow());

        setContentView(R.layout.activity_main);

        //存储权限动态获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
            }
        }


        //创建or打开数据库
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        String user_table = "create table if not exists usertable(_id integer primary key autoincrement, userid text, username text, userhead text,signature text, sex text, experience text)";
        String book_table = "create table if not exists booktable(_id integer primary key autoincrement, path text, title text, cover text)";
        String page_table = "create table if not exists pagetable(_id integer primary key autoincrement, path text, pageseek text)";
        String section_table = "create table if not exists sectiontable(_id integer primary key autoincrement, path text, section text, page integer)";
        String bookmark_table = "create table if not exists bookmarktable(_id integer primary key autoincrement, path text, time text, page integer, section text, words text, title text)";
        String curentpage_table = "create table if not exists curentpagetable(_id integer primary key autoincrement, path text, curentpage integer)";
        db.execSQL(user_table);
        db.execSQL(book_table);
        db.execSQL(page_table);
        db.execSQL(section_table);
        db.execSQL(bookmark_table);
        db.execSQL(curentpage_table);


        fManager = getSupportFragmentManager();
        bindViews();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        homeFragment = new HomeFragment();
        fTransaction.add(R.id.ly_content, homeFragment);
        fTransaction.commit();
        txt_home.setSelected(true);
        flag = 1;

    }

    private void bindViews() {
        txt_home = (TextView) findViewById(R.id.txt_home);
        txt_bookrack = (TextView) findViewById(R.id.txt_bookrack);
        txt_zone = (TextView) findViewById(R.id.txt_zone);
        txt_mine = (TextView) findViewById(R.id.txt_mine);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_home.setOnClickListener(this);
        txt_bookrack.setOnClickListener(this);
        txt_zone.setOnClickListener(this);
        txt_mine.setOnClickListener(this);
    }

    private void setSelected() {
        txt_home.setSelected(false);
        txt_bookrack.setSelected(false);
        txt_zone.setSelected(false);
        txt_mine.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if(homeFragment != null ) fragmentTransaction.hide(homeFragment);
        if(zoneFragment != null ) fragmentTransaction.hide(zoneFragment);
        if(bookrackFragment != null ) fragmentTransaction.hide(bookrackFragment);
        if(mineFragment!= null ) fragmentTransaction.hide(mineFragment);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()) {
            case R.id.txt_home:
                setSelected();
                txt_home.setSelected(true);
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fTransaction.add(R.id.ly_content, homeFragment);
                }
                else {
                    fTransaction.show(homeFragment);
                }
                flag = 1;
                break;
            case R.id.txt_bookrack:
                setSelected();
                txt_bookrack.setSelected(true);
                if (bookrackFragment == null) {
                    bookrackFragment = new BookrackFragment();
                    fTransaction.add(R.id.ly_content, bookrackFragment);
                }
                else {
                    fTransaction.show(bookrackFragment);
                }

                flag = 2;
                break;
            case R.id.txt_zone:
                setSelected();
                txt_zone.setSelected(true);
                if (zoneFragment == null) {
                    zoneFragment = new ZoneFragment();
                    fTransaction.add(R.id.ly_content, zoneFragment);
                }
                else {
                    fTransaction.show(zoneFragment);
                }
                flag = 3;
                break;
            case R.id.txt_mine:
                setSelected();
                txt_mine.setSelected(true);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    fTransaction.add(R.id.ly_content, mineFragment);
                }
                else {
                    fTransaction.show(mineFragment);
                }
                flag = 4;
                break;
        }
        fTransaction.commit();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       *//* Fragment fragment = getFragmentManager().findFragmentById(R.id.ly_content);
        if(flag == 1) {
            WebView web_home = (WebView) fragment.getView().findViewById(R.id.ly_webhome);
            if(keyCode == KeyEvent.KEYCODE_BACK&&web_home.canGoBack()) {
                web_home.goBack();
                return false;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        else if(flag == 3) {
            WebView web_zone = (WebView) fragment.getView().findViewById(R.id.ly_webzonefind);
            if(keyCode == KeyEvent.KEYCODE_BACK&&web_zone.canGoBack()) {
                web_zone.goBack();
                return false;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        else {
            return super.onKeyDown(keyCode, event);
        }*//*
    }*/



    //跳转到扫描书籍active
    public void toSearchBookResult(View source){

    }
}
