package com.example.ebook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ZoneFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private View contentView;
    private TextView titlefind;
    private TextView titleconcern;
    private ViewPager zonecontent;
    private Toolbar toolbar;
    private String userid = "";
    private Boolean flag = false;
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.zone_content, container,false);
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir()+"/ebook.db",null);
        Cursor cursor = db.rawQuery("select * from usertable", null);
        while (cursor.moveToNext()){
            userid = cursor.getString(1);
        }
        initView();
        initContentFragment();
    }

    private void  initView() {
        titlefind = contentView.findViewById(R.id.tv_zonetitlefind);
        titleconcern = contentView.findViewById(R.id.tv_zonetitleconcern);
        zonecontent = contentView.findViewById(R.id.vp_zonecontent);
        toolbar = contentView.findViewById(R.id.tb_zone);

        titleconcern.setOnClickListener(this);
        titlefind.setOnClickListener(this);
    }
    private void initContentFragment(){
        ArrayList<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new ZoneFindFragment());
        mFragmentList.add(new ZoneConcernFragment());
        ZoneFragmentAdapter adapter = new ZoneFragmentAdapter(getChildFragmentManager(),mFragmentList);
        zonecontent.setAdapter(adapter);
        zonecontent.setOffscreenPageLimit(2);
        zonecontent.addOnPageChangeListener(this);
        setCurrentItem(0);
    }

    private void setCurrentItem(int i){
        zonecontent.setCurrentItem(i);
        titlefind.setSelected(false);
        titlefind.setTextSize(14);
        titleconcern.setSelected(false);
        titleconcern.setTextSize(14);
        if(i == 0){
            titlefind.setSelected(true);
            titlefind.setTextSize(20);
        }
        else {
            titleconcern.setSelected(true);
            titleconcern.setTextSize(20);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_zonetitlefind:
                if(zonecontent.getCurrentItem() != 0) {
                    setCurrentItem(0);
                }
                break;
            case R.id.tv_zonetitleconcern:
                if(zonecontent.getCurrentItem() != 1) {
                    setCurrentItem(1);
                }
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setCurrentItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
