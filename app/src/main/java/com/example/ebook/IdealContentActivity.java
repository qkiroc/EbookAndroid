package com.example.ebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ebook.Util.Util;

public class IdealContentActivity extends AppCompatActivity {
    private String idealid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_ideal_content);
        ImageView goback = findViewById(R.id.img_idealcontent_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idealid= bundle.getString("idealid");
        WebView web_view = (WebView)findViewById(R.id.wv_idealcontent);
        Util.initView(web_view);
        web_view.loadUrl(getResources().getString(R.string.url)+"/idealcontent?idealid="+idealid);
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(), "javaToJS");
    }
    public class javaToJS{
        @JavascriptInterface
        public String getUserid(){
            return Util.getUserid(String.valueOf(getFilesDir()));
        }
        @JavascriptInterface
        public String getIdealid() {return idealid;}
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }
}
