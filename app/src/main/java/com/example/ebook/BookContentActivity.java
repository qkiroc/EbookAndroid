package com.example.ebook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.ebook.view.MyWebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class BookContentActivity extends AppCompatActivity {
    private String bookid;
    private MyWebView web_view;
    private String title;
    private String txtpath;
    private String imgpath;
    private Handler handler;
    private Boolean txtflag = false;
    private Boolean imgflag = false;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        getWindow().setStatusBarColor(Color.parseColor("#F8F8F8"));
        setContentView(R.layout.activity_book_content);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bookid = bundle.getString("bookid");
        handler = new Handler(){
            public void handleMessage(Message message) {
                switch (message.what){
                    case 0:
                        Toast.makeText(getApplicationContext(),"开始下载，请稍后",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "没有读写权限", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        txtflag = true;
                        if (imgflag){
                            Toast.makeText(getApplicationContext(), "下载完成",Toast.LENGTH_SHORT).show();
                            addBookToDB();
                        }
                        break;
                    case 3:
                        imgflag = true;
                        if (txtflag){
                            Toast.makeText(getApplicationContext(), "下载完成",Toast.LENGTH_SHORT).show();
                            addBookToDB();
                        }
                        break;
                }
            }
        };
        web_view = findViewById(R.id.wv_bookcontent);
        Util.initView(web_view);
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(),"javaToJS");
        web_view.loadUrl(getResources().getString(R.string.url)+"/bookhtml?bookid="+bookid);
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
        web_view.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                String iurl = url.split("/")[5];
                iurl = iurl.substring(0,iurl.length()-4);
                new Thread(new DownLoadThread(url, 2)).start();
                new Thread(new DownLoadThread(getResources().getString(R.string.url)+"/static/bookcover/"+iurl+".jpg",3)).start();
            }
        });
    }
    public class DownLoadThread implements Runnable {

        private String dlUrl;
        private Integer type;
        public DownLoadThread(String dlUrl, Integer type) {
            this.dlUrl = dlUrl;
            this.type = type;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            InputStream in = null;
            FileOutputStream fout = null;
            try {
                URL httpUrl = new URL(dlUrl);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                in = conn.getInputStream();
                File downloadFile, sdFile;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downloadFile = Environment.getExternalStorageDirectory();
                    String decode = URLDecoder.decode(dlUrl,"UTF-8");
                    String name = decode.split("/")[5];
                    sdFile = new File(downloadFile, name);
                    fout = new FileOutputStream(sdFile);
                    title = name;
                    if(type == 2){
                        txtpath = sdFile.getPath();
                    }
                    else {
                        imgpath = sdFile.getPath();
                    }

                }else{
                    handler.sendEmptyMessage(1);
                }
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            handler.sendEmptyMessage(type);
        }
    }
    public void addBookToDB(){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        ContentValues cValue = new ContentValues();
        cValue.put("path", txtpath);
        cValue.put("title",title.substring(0,title.length()-4));
        cValue.put("cover", imgpath);
        db.insert("booktable",null, cValue);
    }
    public class javaToJS{
        @JavascriptInterface
        public void filishActive(){
            finish();
        }
        @JavascriptInterface
        public String getUserid(){
            return Util.getUserid(String.valueOf(getFilesDir()));
        }
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }
}
