package com.example.ebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.ebook.view.MyWebView;

public class LikeClassifyActivity extends AppCompatActivity {
    private MyWebView web_view;
    private String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_like_classify);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");
        web_view = findViewById(R.id.wv_likeclassify);
        Util.initView(web_view);
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(),"javaToJS");
        web_view.loadUrl(getResources().getString(R.string.url)+"/likeclassify");
        web_view.setOnScrollChangeListener(new MyWebView.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {

            }
            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
            }
        });
    }
    public class javaToJS{
        @JavascriptInterface
        public String getUserid(){
            return userid;
        }
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void finishActive(){
            finish();
        }
    }
}
