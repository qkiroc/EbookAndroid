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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ChangePwdActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText oldpwd;
    private EditText newpwd;
    private EditText newpwd1;
    private String userid;
    private Handler handle;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_change_pwd);

        oldpwd = findViewById(R.id.et_oldpwd);
        newpwd = findViewById(R.id.et_newpwd);
        newpwd1 = findViewById(R.id.et_newpwd1);
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        handle = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        ChangePwdActivity.this.finish();
                }
            }
        };

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String signature = bundle.getString("signature");
        userid = bundle.getString("userid");


        Button save = findViewById(R.id.bt_changepwdsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stroldpwd = String.valueOf(oldpwd.getText());
                String strnewpwd = String.valueOf(newpwd.getText());
                String strnewpwd1 = String.valueOf(newpwd1.getText());
                if(strnewpwd.equals(strnewpwd1)){
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("userid",userid)
                            .add("oldpwd", stroldpwd)
                            .add("password", strnewpwd).build();
                    Request request = new Request.Builder()
                            .url(getResources().getString(R.string.url)+"/changepwd")
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
                                if(obj.getInt("result") == 1) {
                                    handle.sendEmptyMessage(0);
                                }
                                else if(obj.getInt("result") == 2){
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
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
                else {
                    Toast.makeText(getApplicationContext(), "两次输入密码不一致，请确定密码",Toast.LENGTH_SHORT).show();
                }

            }
        });


        ImageView goback = (ImageView) findViewById(R.id.img_changepwdactive_goback);
        goback.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
