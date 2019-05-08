package com.example.ebook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ebook.Util.Picture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MineFragment extends Fragment {
    private SQLiteDatabase db;
    private Cursor cursor;
    private LinearLayout user;
    private TextView username;
    private TextView usersignature;
    private TextView experience;
    private ImageView userhead;
    private TextView concerncount;
    private TextView fanscount;
    private LinearLayout concern;
    private LinearLayout fans;
    private Handler handle;
    private String strusername;
    private String strusersignature;
    private String strexperience;
    private String sex;
    private String userid;
    private String headurl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.mine_content, container,false);
        user = contentView.findViewById(R.id.ly_user);
        username = contentView.findViewById(R.id.tv_username);
        usersignature = contentView.findViewById((R.id.tv_usersignature));
        experience = contentView.findViewById((R.id.tv_experiencecount));
        userhead = contentView.findViewById(R.id.Img_userhead);
        concerncount = contentView.findViewById(R.id.tv_concerncount);
        fanscount = contentView.findViewById(R.id.tv_fanscount);
        concern = contentView.findViewById(R.id.ly_concern);
        fans = contentView.findViewById(R.id.ly_fans);
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        handle = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bitmap bitmap=(Bitmap)msg.obj;
                        Bitmap roundbitmap = Picture.clipSquareBitmap(bitmap, 200, bitmap.getWidth());
                        userhead.setImageBitmap(roundbitmap);
                        break;
                    case 1:
                        concerncount.setText((String) msg.obj);
                        break;
                    case 2:
                        fanscount.setText((String) msg.obj);
                        break;
                    case 3:
                        concerncount.setText("0");
                        break;
                    case 4:
                        fanscount.setText("0");
                        break;
                }
            };
        };

        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir()+"/ebook.db",null);
        cursor = db.rawQuery("select * from usertable", null);
        if(cursor.getCount() == 1) {
            while (cursor.moveToNext()){
                strusername = cursor.getString(2);
                strusersignature = cursor.getString(4);
                strexperience = cursor.getString(6);
                sex = cursor.getString(5);
                userid = cursor.getString(1);
                headurl = cursor.getString(3);
                username.setText(strusername);
                usersignature.setText(strusersignature);
                experience.setText(strexperience);
            }
            if(headurl.length() > 0){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = Picture.getHttpBitmap(headurl);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = bmp;
                        handle.sendMessage(msg);
                    }
                }).start();
            }
        }
        else {
            username.setText("未登录");
            usersignature.setText("这个人很懒，还没有设置签名");
            experience.setText("0");
            concerncount.setText("0");
            fanscount.setText("0");
            userhead.setImageResource(R.mipmap.defaultuserhead);
            userid = "";
        }

        OkHttpClient clientconcern = new OkHttpClient();
        Request requestconcern = new Request.Builder()
                                .url(getResources().getString(R.string.url)+"/getconcerncount?userid="+userid).get().build();
        Call callconcern = clientconcern.newCall(requestconcern);
        callconcern.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String d = response.body().string();
                try {
                    JSONObject object = new JSONObject(d);
                    if(object.getInt("result") == 1){
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = object.getString("data");
                        handle.sendMessage(msg);
                    }
                    else {
                        handle.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        OkHttpClient clientfans = new OkHttpClient();
        Request requestfans = new Request.Builder()
                .url(getResources().getString(R.string.url)+"/getfanscount?userid="+userid).get().build();
        Call callfans = clientfans.newCall(requestfans);
        callfans.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String d = response.body().string();
                try {
                    JSONObject object = new JSONObject(d);
                    if(object.getInt("result") == 1){
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = object.getString("data");
                        handle.sendMessage(msg);
                    }
                    else {
                        handle.sendEmptyMessage(4);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //跳转到用户设置active
        String finalStrusername = strusername;
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (cursor.getCount() == 1) {
                    intent = new Intent(getActivity(), UserSettingActivity.class);
                    Bundle bd = new Bundle();
                    bd.putString("username",strusername);
                    bd.putString("userid", userid);
                    bd.putString("signature", strusersignature);
                    bd.putString("sex", sex);
                    bd.putString("userhead", headurl);
                    intent.putExtras(bd);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //跳转到我的关注
        concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cursor.getCount() == 1){
                    Intent intent = new Intent(getActivity(), ConcernActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "concern");
                    bundle.putString("userid", userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        //跳转到我的粉丝
        fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cursor.getCount() == 1){
                    Intent intent = new Intent(getActivity(), ConcernActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "fans");
                    bundle.putString("userid", userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
