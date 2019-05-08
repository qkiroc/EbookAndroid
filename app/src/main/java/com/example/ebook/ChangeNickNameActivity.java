package com.example.ebook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;

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

public class ChangeNickNameActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText changeusername;
    private String strchangeusername;
    private Handler handle;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_change_nick_name);
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        handle = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        db.execSQL("update usertable set username = '"+strchangeusername+ "' where userid = '"+Util.getUserid(String.valueOf(getFilesDir()))+"'");
                        Intent intent = new Intent();
                        intent.setClass(ChangeNickNameActivity.this, UserSettingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", strchangeusername);
                        intent.putExtra("bundle",bundle);
                        setResult(0, intent);
                        ChangeNickNameActivity.this.finish();
                }
            }
        };



        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String username = bundle.getString("username");
        changeusername = findViewById(R.id.et_changeusername);
        changeusername.setText(username);
        ImageView goback = (ImageView) findViewById(R.id.img_changenicknameactive_goback);
        goback.setOnClickListener(this);

        TextView save = (TextView) findViewById((R.id.tv_changenicknameactive_save));
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strchangeusername = String.valueOf(changeusername.getText());
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("userid",Util.getUserid(String.valueOf(getFilesDir())))
                        .add("username", strchangeusername).build();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/changename")
                        .post(body).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String d = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(d);
                            if(obj.getInt("result") == 1){
                                handle.sendEmptyMessage(0);
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "修改失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
