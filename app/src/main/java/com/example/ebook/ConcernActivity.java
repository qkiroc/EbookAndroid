package com.example.ebook;

import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;

public class ConcernActivity extends AppCompatActivity {
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_concern);
        ImageView goback = findViewById(R.id.img_concern_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        type = bundle.getString("type");
        if(type.equals("fans")){
            TextView title = findViewById(R.id.tv_concerntitle);
            title.setText("我的粉丝");
        }
        WebView web_view = (WebView)findViewById(R.id.wv_concern);
        Util.initView(web_view);
        web_view.loadUrl(getResources().getString(R.string.url)+"/concernhtml");
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(), "javaToJS");
    }
    public class javaToJS{
        @JavascriptInterface
        public String getUserid(){
            return Util.getUserid(String.valueOf(getFilesDir()));
        }
        @JavascriptInterface
        public String getType(){
            return type;
        }
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }
}
