package com.example.ebook;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Picture;
import com.example.ebook.Util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView userhead;
    private TextView textSex;
    private TextView userid;
    private TextView username;
    private Handler handle;
    private String strusername;
    private String strusersignature;
    private String sex;
    private String struserid;
    private String headurl;
    private String strchangsex;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initWindow(getWindow());

        setContentView(R.layout.activity_user_setting);

        //返回销毁当前active
        ImageView goback = (ImageView) findViewById(R.id.img_goBackActive);
        goback.setOnClickListener(this);
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/ebook.db",null);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        strusername = bd.getString("username");
        struserid = bd.getString("userid");
        strusersignature = bd.getString("signature");
        sex = bd.getString("sex");
        headurl = bd.getString("userhead");

        userhead = findViewById(R.id.img_usersettinguserhead);
        handle = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bitmap bitmap=(Bitmap)msg.obj;
                        Bitmap roundbitmap = Picture.clipSquareBitmap(bitmap, 200, bitmap.getWidth());
                        userhead.setImageBitmap(roundbitmap);
                        break;
                    case 1:
                        textSex.setText(String.valueOf(msg.obj));
                        db.execSQL("update usertable set sex = '"+String.valueOf(msg.obj)+ "' where userid = '"+struserid+"'");

                }
            };
        };
        if (headurl.length() >0 ){
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
        userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("outputFormat", "JPEG");// 返回格式
                startActivityForResult(intent, 0);
            }
        });

        userid = findViewById(R.id.tv_usersettingid);
        userid.setText(struserid);
        username = findViewById(R.id.tv_changenickname);
        username.setText(strusername);

        //修改性别
        RelativeLayout changesex = (RelativeLayout) findViewById(R.id.rl_changesex);
        textSex= (TextView) findViewById(R.id.tv_changesex);
        if(!sex.equals("2")){
            textSex.setText(sex);
        }
        changesex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] sex = new String[]{"男","女"};
                AlertDialog alert = null;
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingActivity.this);
                alert = builder.setTitle("修改性别")
                        .setItems(sex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                strchangsex = sex[which];
                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("userid",struserid)
                                        .add("sex",strchangsex).build();
                                Request request = new Request.Builder()
                                        .url(getResources().getString(R.string.url)+"/changesex")
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
                                                Message msg = new Message();
                                                msg.what = 1;
                                                msg.obj = strchangsex;
                                                handle.sendMessage(msg);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "修改失败，请稍后再试", Toast.LENGTH_SHORT);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        }).create();
                alert.show();
            }
        });

        //退出登录
        RelativeLayout loginout = findViewById(R.id.rl_usersettingloginout);
        loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("delete from usertable where userid = '" + struserid+"'");
                finish();
            }
        });

        //修改昵称
        RelativeLayout changenickname = (RelativeLayout) findViewById(R.id.rl_changenickname);
        //final TextView textnickname = (TextView) findViewById(R.id.tv_changenickname);
        changenickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingActivity.this, ChangeNickNameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", strusername);
                bundle.putString("userid", struserid);
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });

        //修改签名
        RelativeLayout changesignature = (RelativeLayout) findViewById(R.id.rl_changesignature);
        //final TextView textnickname = (TextView) findViewById(R.id.tv_changenickname);
        changesignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingActivity.this, ChangeSignatureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("signature", strusersignature);
                bundle.putString("userid", struserid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //修改密码
        RelativeLayout changepwd = (RelativeLayout) findViewById(R.id.rl_changepwd);
        changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingActivity.this, ChangePwdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", struserid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Log.d("数据", String.valueOf(cr.openInputStream(uri)));
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bitmap;
                handle.sendMessage(msg);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        if (resultCode == 0 && requestCode == 0) {
            if(!String.valueOf(data).equals("null")){
                Bundle bundle = data.getBundleExtra("bundle");
                username.setText(bundle.getString("username"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
