package com.example.ebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_login);
        ImageView close = findViewById(R.id.img_loginclose);
        close.setOnClickListener(this);

        TextView nowsignin = findViewById(R.id.tv_loginsignin);
        nowsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(LoginActivity.this, signInActivity.class);
                startActivity(intent);
            }
        });

        TextView button = findViewById(R.id.tv_loginbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etname = findViewById(R.id.et_loginusername);
                EditText etpassword = findViewById(R.id.et_loginpassword);
                String name = String.valueOf(etname.getText());
                String password = String.valueOf(etpassword.getText());
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("username",name)
                        .add("password",password).build();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/login")
                        .post(body).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            String d = response.body().string();
                            try {
                                JSONObject result = new JSONObject(d);
                                if(result.getInt("result") == 0) {
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(),"书友ID或密码错误",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    JSONObject data = new JSONObject(result.getString("data"));
                                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
                                    ContentValues cValue = new ContentValues();
                                    cValue.put("username", data.getString("username"));
                                    cValue.put("signature",data.getString("signature"));
                                    cValue.put("experience", data.getString("experience"));
                                    cValue.put("sex", data.getString("sex"));
                                    cValue.put("userhead",data.getString("userhead"));
                                    cValue.put("userid", data.getString("userid"));
                                    db.insert("usertable",null,cValue);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Log.d("请求",d);
                        }
                      //  Log.d("结果",response.body().string());
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
