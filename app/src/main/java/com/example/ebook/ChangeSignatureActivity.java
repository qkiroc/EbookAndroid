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

public class ChangeSignatureActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText changesignature;
    private String userid;
    private String strchangesignature;
    private Handler handle;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());
        setContentView(R.layout.activity_change_signature);

        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);
        handle = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        db.execSQL("update usertable set signature = '"+strchangesignature+ "' where userid = '"+userid+"'");
                        ChangeSignatureActivity.this.finish();
                }
            }
        };

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String signature = bundle.getString("signature");
        userid = bundle.getString("userid");
        changesignature = findViewById(R.id.et_signature);
        if(!signature.equals("这人很懒，还没有设置签名")){
            changesignature.setText(signature);
        }



        ImageView goback = (ImageView) findViewById(R.id.img_changesignatureactive_goback);
        goback.setOnClickListener(this);

        TextView save = (TextView) findViewById((R.id.tv_changesignatureactive_save));
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strchangesignature = String.valueOf(changesignature.getText());
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("userid",userid)
                        .add("signature", strchangesignature).build();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/changesignature")
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
