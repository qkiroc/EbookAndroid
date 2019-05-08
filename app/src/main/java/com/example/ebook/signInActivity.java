package com.example.ebook;

import android.content.ContentValues;
import android.database.Cursor;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class signInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_sign_in);
        ImageView close = findViewById(R.id.img_signinclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView button = findViewById(R.id.tv_signinbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.et_signinusername);
                EditText password = findViewById(R.id.et_signinpassword);
                EditText passwordagain = findViewById(R.id.et_signinpasswordagain);
                String strusername = String.valueOf(username.getText());
                String strpassword = String.valueOf(password.getText());
                String strpasswordagain = String.valueOf(passwordagain.getText());
                if(!strpassword.equals(strpasswordagain)){
                    Toast.makeText(getApplicationContext(),"两次密码输入不一致，请再次确定密码",Toast.LENGTH_SHORT).show();
                }
                else {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("username",strusername)
                            .add("password",strpassword).build();
                    Request request = new Request.Builder()
                            .url(getResources().getString(R.string.url)+"/signin")
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
                                        Toast.makeText(getApplicationContext(),"注册失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    else {
                                        JSONObject data = new JSONObject(result.getString("data"));
                                        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
                                        ContentValues cValue = new ContentValues();
                                        cValue.put("username", data.getString("username"));
                                        cValue.put("signature","这个人很懒，还没有设置签名");
                                        cValue.put("experience", 0);
                                        cValue.put("sex", 2);
                                        cValue.put("userhead","");
                                        cValue.put("userid", data.getString("ebookid"));
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
            }
        });
    }
}
