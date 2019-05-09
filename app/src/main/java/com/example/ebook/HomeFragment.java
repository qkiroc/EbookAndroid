package com.example.ebook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private TextView titlecommend;
    private TextView titleclassify;
    private ViewPager homecontent;
    private View contentView;
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.home_content, container,false);
        RelativeLayout search = contentView.findViewById(R.id.rl_homesearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        initView();
        initContentFragment();
        return contentView;
    }

    private void  initView() {
        titlecommend = contentView.findViewById(R.id.tv_homerecommend);
        titleclassify = contentView.findViewById(R.id.tv_homeclassify);
        homecontent = contentView.findViewById(R.id.vp_homecontent);

        titlecommend.setOnClickListener(this);
        titleclassify.setOnClickListener(this);
    }
    private void initContentFragment(){
        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeCommendFragment());
        mFragmentList.add(new HomeClassifyFragment());
        ZoneFragmentAdapter adapter = new ZoneFragmentAdapter(getChildFragmentManager(),mFragmentList);
        homecontent.setAdapter(adapter);
//        homecontent.setOffscreenPageLimit(1);
        homecontent.addOnPageChangeListener(this);
        setCurrentItem(0);
    }

    private void setCurrentItem(int i){
        homecontent.setCurrentItem(i);
        titlecommend.setSelected(false);
        titlecommend.setTextSize(14);
        titleclassify.setSelected(false);
        titleclassify.setTextSize(14);
        if(i == 0){
            titlecommend.setSelected(true);
            titlecommend.setTextSize(20);
        }
        else {
            titleclassify.setSelected(true);
            titleclassify.setTextSize(20);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_homerecommend:
                if(homecontent.getCurrentItem() != 0) {
                    setCurrentItem(0);
                }
                break;
            case R.id.tv_homeclassify:
                if(homecontent.getCurrentItem() != 1) {
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
