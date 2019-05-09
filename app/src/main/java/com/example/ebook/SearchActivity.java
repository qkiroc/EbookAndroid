package com.example.ebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.ebook.view.MyWebView;

public class SearchActivity extends AppCompatActivity {
    private EditText search;
    private MyWebView web_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_search);
        search = findViewById(R.id.et_search);
        web_view = findViewById(R.id.wv_search);
        Util.initView(web_view);
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(),"javaToJS");
        web_view.loadUrl(getResources().getString(R.string.url)+"/search");
        web_view.setOnScrollChangeListener(new MyWebView.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                web_view.evaluateJavascript("javascript:loadcontent()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {}
                });
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
            }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String key = search.getText().toString();
                    if(TextUtils.isEmpty(key)){
                        Toast.makeText(getApplicationContext(),"请输入关键字搜索",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(search.getWindowToken(),0);
                        }
                        web_view.evaluateJavascript("javascript:search('"+key+"')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {}
                        });
                    }
                }
                return false;
            }
        });
    }
    public class javaToJS{
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void toContent(String bookid){
            Intent intent = new Intent(SearchActivity.this, BookContentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("bookid",bookid);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
