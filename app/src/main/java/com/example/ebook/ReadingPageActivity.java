package com.example.ebook;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.library.OnSelectListener;
import com.example.library.SelectableTextHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReadingPageActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private String path;
    private Calendar calendar;
    private Integer hour;
    private Integer minute;
    private Integer battery_level;
    private Integer battery_scale;
    private String booktitle;
    private String charset;
    private TextView time;
    private RelativeLayout readpageclick;
    private TextView textcontent;
    private TextView textpage;
    private TextView idealtext;
    private LinearLayout bottomlan;
    private LinearLayout toplan;
    private LinearLayout readingpagelight;
    private LinearLayout readingpagerightbg;
    private LinearLayout readdingpagerightlan;
    private RelativeLayout readingpagebg;
    private RelativeLayout readingpagebg1;
    private RelativeLayout readingpagebg2;
    private LinearLayout idealbg;
    private ListView catalogview;
    private ListView readmarkview;
    private ImageView catalogbt;
    private ImageView readbookmarkbt;
    private SeekBar seekBar;
    private int pages;
    private int curentpage = 0;
    private int curentbrightness;
    private boolean isshowlan = false;
    private boolean isshowrightlan = false;
    private boolean iscatalog = true;
    private boolean isshowlight = false;
    private boolean isshowideal = false;
    private boolean islightfollowsystem = false;
    private List<Integer> pageSeeks = new ArrayList<Integer>();
    private List<Integer> firstpageSeeks = new ArrayList<Integer>();
    private LinkedHashMap<String,Integer> sections = new LinkedHashMap<>();
    private Set<Integer> readmark = new HashSet<Integer>();
    private Integer nowsetion = 0;
    private RandomAccessFile file;
    private SQLiteDatabase db;
    private SelectableTextHelper mSelectableTextHelper;

    private long touchstarttime;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            // window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_reading_page);


        db = SQLiteDatabase.openOrCreateDatabase("/data/user/0/com.example.ebook/files/ebook.db",null);

        //获取当前时间
        time = findViewById(R.id.tv_readingpagetime);
        final Handler timehanddle = new Handler(){
            public void handleMessage(Message msg) {
                time.setText((String)msg.obj);
            }
        };

        new Thread() {
            public void run (){
                try {
                    while (true) {
                        calendar = Calendar.getInstance();
                        if (calendar.get(Calendar.AM_PM) == 0) {
                            hour = calendar.get(Calendar.HOUR);
                        }
                        else {
                            hour = calendar.get(Calendar.HOUR) + 12;
                        }
                        minute = calendar.get(Calendar.MINUTE);
                        String time;
                        if (hour<10) {
                            time = "0" + hour + ":";
                        }
                        else{
                            time = hour +":";
                        }
                        if(minute<10){
                            time += "0" + minute;
                        }
                        else {
                            time += minute;
                        }
                        timehanddle.sendMessage(timehanddle.obtainMessage(100, time));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }.start();

        //获取电池电量
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    battery_level = intent.getIntExtra("level",0) * 100;
                    battery_scale = intent.getIntExtra("scale",100);
                    ProgressBar battery = findViewById(R.id.vi_readingpagebattery);
                    battery.setProgress(battery_level/battery_scale);
                }
            }
        };
        registerReceiver(receiver,filter);

        readingpagerightbg = findViewById(R.id.rl_readingpagerightbg);
        readingpagebg = findViewById(R.id.ly_readingpagebg);
        readingpagebg1 = findViewById(R.id.ly_readingpagebg1);
        readingpagebg2 = findViewById(R.id.ly_readingpagebg2);
        idealbg = findViewById(R.id.ly_idear);
        catalogview = findViewById(R.id.lv_catalog);
        readmarkview = findViewById(R.id.lv_bookmark);
        catalogbt = findViewById(R.id.img_catalogbt);
        readbookmarkbt = findViewById(R.id.img_readbookmarkbt);
        readdingpagerightlan = findViewById(R.id.rl_readingpagerightlan);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        textpage= findViewById(R.id.tv_readingpagepage);
        readpageclick = findViewById(R.id.rl_readingpagecontent);
        textcontent = findViewById(R.id.tv_readingpagecontent);
        textcontent.setOnTouchListener(this);

        mSelectableTextHelper = new SelectableTextHelper.Builder(textcontent)
                //.setSelectedColor(getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                //.setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                .build();

        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }

            @Override
            public void onClickCopy(CharSequence content) {

            }

            @Override
            public void onClickIdeal(CharSequence content) {
                isShowIdeal(content.toString());
            }
        });

        //打开文件
        try {
            file = new RandomAccessFile(path,"r");
            File filegetname = new File(path);
            charset = getCharset(filegetname);
            booktitle = filegetname.getName();
            addBookmark();
            Cursor pagecursor = db.rawQuery("select * from pagetable where path = '" + path +"'", null);
            if (pagecursor.getCount() == 1) {
                loadagain(pagecursor);
            }
            else {
                initPage();
                loadPage();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bottomlan = findViewById(R.id.rl_readingpagebottom);
        toplan = findViewById(R.id.rl_readingpagetop);
        readingpagelight = findViewById(R.id.ly_readingpagelight);
        RelativeLayout readingpagecatalogbt = findViewById(R.id.rl_readingpagecatalogbt);
        RelativeLayout readingpagelightbt = findViewById(R.id.rl_readingpagelightbt);
        RelativeLayout readingpageaddmarkbt = findViewById(R.id.rl_readingpageaddmarkbt);

        readingpagecatalogbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowRightLan();
                isShowTopAndBottomLan();
            }
        });
        readingpagelightbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowChangeLight();
                isShowTopAndBottomLan();
            }
        });
        readingpageaddmarkbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowTopAndBottomLan();
                if(!readmark.contains(curentpage)){
                    int i = 0;
                    String sectiontitle = "";
                    for(Map.Entry<String,Integer> section : sections.entrySet()) {
                        if(i == nowsetion){
                            sectiontitle = section.getKey();
                            break;
                        }
                        i++;
                    }
                    Long timestamp = System.currentTimeMillis();
                    String text = String.valueOf(textcontent.getText());
                    ContentValues cValuemark= new ContentValues();
                    cValuemark.put("path", path);
                    cValuemark.put("time", timestamp);
                    cValuemark.put("page",curentpage);
                    cValuemark.put("section", sectiontitle);
                    cValuemark.put("words", text);
                    cValuemark.put("title",booktitle.substring(0,booktitle.length()-4));
                    db.insert("bookmarktable",null, cValuemark);
                }
                else {
                    db.execSQL("delete from bookmarktable where path = '" + path +"'" + " and page='"+curentpage+"'");
                }
                addBookmark();
                isShowMark(curentpage);
            }
        });

        bottomlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        toplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        readingpagelight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        readingpagebg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowTopAndBottomLan();
            }
        });
        readingpagebg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowChangeLight();
            }
        });
        readingpagebg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowIdeal(null);
            }
        });
        readingpagerightbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowRightLan();
            }
        });
        idealbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView idealpusth = findViewById(R.id.bt_idealpush);
        idealpusth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView idealedittext = findViewById(R.id.et_idealedittext);
                String stridealedittext = String.valueOf(idealedittext.getText());
                if(stridealedittext.length()>0){
                    Cursor cursor = db.rawQuery("select * from usertable", null);
                    if(cursor.getCount() == 1){
                        String userid = null;
                        String username = null;
                        String userhead = null;
                        while (cursor.moveToNext()){
                            userid = cursor.getString(1);
                            username = cursor.getString(2);
                            userhead = cursor.getString(3);
                        }
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("userid",userid)
                                .add("username", username)
                                .add("userhead",userhead)
                                .add("quote", (String) idealtext.getText()+"————"+booktitle.substring(0,booktitle.length()-4))
                                .add("content",stridealedittext).build();
                        Request request = new Request.Builder()
                                .url(getResources().getString(R.string.url)+"/publish")
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
                                    JSONObject re = new JSONObject(d);
                                    if (re.getInt("result") == 1) {
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                                        isShowIdeal(null);
                                        Looper.loop();

                                    }
                                    else {
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "发送失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"说点什么再发送吧",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final TextView lightfollowsystem = findViewById(R.id.tv_lightfollowsystem);
        final TextView setnightpattern = findViewById(R.id.tv_setnightpattern);
        seekBar = findViewById(R.id.sb_readingpagelight);
        seekBar.setMax(255);
        curentbrightness = getSystemBrightness();
        setnightpattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setnightpattern.isSelected()){
                    setnightpattern.setSelected(false);
                    textcontent.setTextColor(Color.parseColor("#000000"));
                    readpageclick.setBackgroundResource(R.mipmap.readpagebg0);
                }
                else {
                    setnightpattern.setSelected(true);
                    textcontent.setTextColor(Color.parseColor("#D1D1D1"));
                    readpageclick.setBackgroundResource(R.mipmap.readpagebg1);
                }
            }
        });
        lightfollowsystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islightfollowsystem){
                    lightfollowsystem.setSelected(false);
                    islightfollowsystem = false;
                }
                else{
                    int systembrightness = getSystemBrightness();
                    changeAppBrightness(systembrightness);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBar.setProgress(systembrightness,true);
                    }
                    islightfollowsystem = true;
                    lightfollowsystem.setSelected(true);
                }

            }
        });
        seekBar.setProgress(curentbrightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeAppBrightness(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lightfollowsystem.setSelected(false);
                islightfollowsystem = false;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        catalogbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!iscatalog){
                    catalogview.setVisibility(View.VISIBLE);
                    readmarkview.setVisibility(View.GONE);
                    catalogbt.setColorFilter(Color.parseColor("#FF9800"));
                    readbookmarkbt.setColorFilter(Color.parseColor("#9B9E79"));
                    iscatalog = true;
                }
            }
        });
        readbookmarkbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iscatalog){
                    catalogview.setVisibility(View.GONE);
                    readmarkview.setVisibility(View.VISIBLE);
                    readbookmarkbt.setColorFilter(Color.parseColor("#FF9800"));
                    catalogbt.setColorFilter(Color.parseColor("#9B9E79"));
                    iscatalog = false;
                }
            }
        });
        catalogview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView pagenumberview = (TextView)view.findViewById(R.id.listitem_catalogpagenumber);
                String pagenumber = (String) pagenumberview.getText();
                changePage(Integer.parseInt(pagenumber)-1);
                isShowRightLan();
            }
        });
        readmarkview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView pagenumberview = (TextView)view.findViewById(R.id.listitem_bookmarkpage);
                String pagenumber = (String) pagenumberview.getText();
                changePage(Integer.parseInt(pagenumber)-1);
                isShowRightLan();
            }
        });

        ImageView goback = findViewById(R.id.img_readpageactive_goback);
        goback.setOnClickListener(this);
    }

    public String getCharset(File file) {
        String code = null;
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
            int p = (bin.read() << 8) + bin.read();
            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xd0a:
                    code = "utf-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    //再次打开文本
    public void loadagain(Cursor pagecursor){
        String[] pageseekstrs = {};
        String pageseekstr = "";
        while (pagecursor.moveToNext()) {
            pageseekstr = pagecursor.getString(2);
        }
        pageseekstr = pageseekstr.substring(1,pageseekstr.length()-1).replace(" ","");
        pageseekstrs = pageseekstr.split(",");
        for(String str : pageseekstrs) {
            pageSeeks.add(Integer.valueOf(str));
        }
        pages = pageSeeks.size();
        Cursor sectioncusor = db.rawQuery("select * from sectiontable where path = '" + path +"'", null);
        while (sectioncusor.moveToNext()) {
            sections.put(sectioncusor.getString(2), Integer.valueOf(sectioncusor.getString(3)));
        }

        Cursor curentpagecusor = db.rawQuery("select * from curentpagetable where path = '" +path+ "'",null);
        Integer nowcurentpage = 0;
        while (curentpagecusor.moveToNext()) {
            nowcurentpage = Integer.valueOf(curentpagecusor.getString(2));
        }

        changePage(nowcurentpage);
        addCatalog();
    }
    //初始化前几页
    public void initPage(){
        String words,pagewords;
        words = getTxtString(0, 10000);
        firstpageSeeks = getPagecontents(words);
        pagewords = getTxtString(firstpageSeeks.get(0), firstpageSeeks.get(1));
        textcontent.setText(pagewords);
    }

    //统计页数
    public void loadPage(){
        final Handler pagehanddle = new Handler(){
            public void handleMessage(Message msg) {
                textpage.setText(curentpage+1+"/"+pages);
                addCatalog();
                ContentValues cValuepage = new ContentValues();
                cValuepage.put("path", path);
                cValuepage.put("pageseek", String.valueOf(pageSeeks));
                db.insert("pagetable",null, cValuepage);

                for (Map.Entry<String, Integer> section: sections.entrySet()){
                    ContentValues cValuesection = new ContentValues();android:
                    cValuesection.put("path", path);
                    cValuesection.put("section", section.getKey());
                    cValuesection.put("page", section.getValue());
                    db.insert("sectiontable",null, cValuesection);
                }
            }
        };
        new Thread(){
            public void run(){
                Integer bytescount;
                String words;
                try {
                    bytescount = (int)file.length();
                    words = getTxtString(0, bytescount);
                    pageSeeks = getPagecontents(words);
                    pages = pageSeeks.size();
                    pagehanddle.sendEmptyMessage(0);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public String getTxtString(Integer start, Integer bytei) {
        start = start == 1 ? 0:start;

        try {
            String words;
            byte[] buffer;
            int i = 0;
            do {
                file.seek(start);
                buffer = new byte[bytei];
                file.read(buffer);
                words = new String(buffer, charset);
                bytei++;
                i++;
                if(i>12){
                    break;
                }
            }
            while (!java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(words));
            return words;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }
    public List<Integer> getPagecontents(String datas) {
        List<String> paragphdatas = new ArrayList<String>();
        List<String> linecontents = new ArrayList<String>();
        List<Integer> pageSeek = new ArrayList<Integer>();
        if (datas != null) {
            String[] ps = datas.split("\r\n");
            for(int i = 0; i < ps.length; i++) {
                paragphdatas.add(ps[i]);
            }
        }
        else {
            paragphdatas.add(datas);
        }
        int linecount = 0;
        for(String paragph : paragphdatas) {
            if (paragph.length() > 0 ){
                while (paragph.length()>0){
                    if(paragph.length()>19) {
                        linecontents.add(paragph.substring(0,19));
                        String newparagph = "";
                        for(int i = 19; i < paragph.length(); i++){
                            newparagph += paragph.charAt(i);
                        }
                        paragph = newparagph;
                    }
                    else {
                        linecontents.add(paragph+"\r\n");
                        paragph = "";
                    }
                }
            }
            else {
                linecontents.add("\r\n");
            }

            linecount += Math.ceil(paragph.length() / 19);
        }
        int i = 0;
        int j = 0;
        String pagecontent = "";
        pageSeek.add(0);
        for (String linecontent : linecontents) {
            String regex = "第.{1,7}章.{0,}\r\n";
            if(!Pattern.matches(regex, linecontent)){
                pagecontent += linecontent;
                if ( i < 15) {
                    i++;
                }
                else {
                    pageSeek.add(pageSeek.get(j)+countByte(pagecontent));
                    pagecontent = "";
                    i = 0;
                    j++;
                }
            }
            else {
                pageSeek.add(pageSeek.get(j)+countByte(pagecontent));
                pagecontent = "";
                i = 1;
                j++;
                pagecontent += linecontent;
                if(!sections.containsKey(linecontent)){
                    sections.put(linecontent.substring(0,linecontent.length()-2),pageSeek.size());
                }
            }

        }
        return pageSeek;
    }

    public void changePage(int page){
        curentpage = page;
        isShowMark(page);
        if(pageSeeks.size() > page) {
            textpage.setText(page+1+"/"+pages);
            int start = pageSeeks.get(page);
            int bytei = pageSeeks.get(page+1) - start;
            int i = 0;
            for (Map.Entry<String, Integer> section : sections.entrySet()){
                if (i < 2 && page < section.getValue()-1) {
                    nowsetion = 0;
                    break;
                }
                if (page < section.getValue()-1) {
                    nowsetion = i-1;
                    break;
                }
                i++;
            }
            addCatalog();
            textcontent.setText(getTxtString(start,bytei));
        }
        else  if(page < firstpageSeeks.size()){
            int start = firstpageSeeks.get(page);
            int bytei = firstpageSeeks.get(page+1) - start;
            textcontent.setText(getTxtString(start,bytei));
        }
    }

    public int countByte(String str){
        int countbyte = 0;
        try {
            countbyte = str.getBytes(charset).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return countbyte;
    }
    public void isShowTopAndBottomLan(){
        Window window = getWindow();
        if (!isshowlan) {
            readingpagebg.setVisibility(View.VISIBLE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            final TranslateAnimation bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            bottomlan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomlan.setVisibility(View.VISIBLE);
                    bottomlan.startAnimation(bottomAnimation);
                }
            }, 0);
            final TranslateAnimation topAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, -1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            topAnimation.setDuration(300l);     //设置动画的过渡时间
            toplan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toplan.setVisibility(View.VISIBLE);
                    toplan.startAnimation(topAnimation);
                }
            }, 0);
            isshowlan = true;
        }
        else {

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            final TranslateAnimation bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            bottomlan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomlan.setVisibility(View.GONE);
                    bottomlan.startAnimation(bottomAnimation);
                }
            }, 0);
            final TranslateAnimation topAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, -1);
            topAnimation.setDuration(300l);     //设置动画的过渡时间
            toplan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toplan.setVisibility(View.GONE);
                    toplan.startAnimation(topAnimation);
                }
            }, 0);
            readingpagebg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagebg.setVisibility(View.GONE);
                }
            },300);
            isshowlan = false;
        }
    }
    public void isShowChangeLight() {
        if(!isshowlight){
            readingpagebg1.setVisibility(View.VISIBLE);
            final TranslateAnimation  bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            readingpagelight.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagelight.setVisibility(View.VISIBLE);
                    readingpagelight.startAnimation(bottomAnimation);
                }
            }, 0);
            isshowlight = true;
        }
        else {
            final TranslateAnimation  bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            readingpagelight.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagelight.setVisibility(View.GONE);
                    readingpagelight.startAnimation(bottomAnimation);
                }
            }, 0);
            readingpagebg1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagebg1.setVisibility(View.GONE);
                }
            },300);
            isshowlight = false;
        }
    }
    public void isShowRightLan(){
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (!isshowrightlan) {
            readingpagerightbg.setVisibility(View.VISIBLE);
            final TranslateAnimation rightAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, -1, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0);
            rightAnimation.setDuration(300l);     //设置动画的过渡时间
            readdingpagerightlan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readdingpagerightlan.setVisibility(View.VISIBLE);
                    readdingpagerightlan.startAnimation(rightAnimation);
                }
            }, 0);
            isshowrightlan = true;
        }
        else {
            final TranslateAnimation rightAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, -1,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0);
            rightAnimation.setDuration(300l);     //设置动画的过渡时间
            readdingpagerightlan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readdingpagerightlan.setVisibility(View.GONE);
                    readdingpagerightlan.startAnimation(rightAnimation);

                }
            }, 0);
            readingpagerightbg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagerightbg.setVisibility(View.GONE);
                }
            },300);
            isshowrightlan = false;
        }
    }
    public void isShowIdeal(String string){
        if(!isshowideal){
            idealtext = findViewById(R.id.tv_idealsentence);
            idealtext.setText("引用："+string);
            TextView idealedittext = findViewById(R.id.et_idealedittext);
            idealedittext.setText(null);
            readingpagebg2.setVisibility(View.VISIBLE);
            final TranslateAnimation  bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            idealbg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    idealbg.setVisibility(View.VISIBLE);
                    idealbg.startAnimation(bottomAnimation);
                }
            }, 0);
            isshowideal = true;
        }
        else {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View v = getWindow().peekDecorView();
            if (null != v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            final TranslateAnimation  bottomAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            bottomAnimation.setDuration(300l);     //设置动画的过渡时间
            idealbg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    idealbg.setVisibility(View.GONE);
                    idealbg.startAnimation(bottomAnimation);
                }
            }, 0);
            readingpagebg2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readingpagebg2.setVisibility(View.GONE);
                }
            },300);
            isshowideal = false;
        }
    }
    public void isShowMark(Integer page) {
        ImageView readmarkimg = findViewById(R.id.img_readbookmark);
        TextView readingmarktitle = findViewById(R.id.tv_readingpagemarktitle);
        if(readmark.contains(page)) {
            readingmarktitle.setText("删除书签");
            readmarkimg.setVisibility(View.VISIBLE);
        }
        else {
            readingmarktitle.setText("添加书签");
            readmarkimg.setVisibility(View.GONE);
        }
    }
    public void addCatalog(){
        LinkedList<ReadingPageCatalogList> mData = new LinkedList<ReadingPageCatalogList>();
        int i = 0;
        for (Map.Entry<String,Integer> section : sections.entrySet()) {
            boolean isnoewsection = false;
            if (i == nowsetion){
                isnoewsection = true;
            }
            i++;
            mData.add(new ReadingPageCatalogList(section.getKey(),section.getValue(), isnoewsection));
        }
        ReadingPageCatalogAdapter adapter = new ReadingPageCatalogAdapter(mData, ReadingPageActivity.this);
        catalogview.setAdapter(adapter);
    }
    public void addBookmark(){
        readmark.clear();
        Cursor markcursor = db.rawQuery("select * from bookmarktable where path = '" + path +"'", null);
        LinkedList<ReadingPageBookmarkList> mData = new LinkedList<ReadingPageBookmarkList>();
        while (markcursor.moveToNext()){
            mData.add(new ReadingPageBookmarkList(markcursor.getString(4), markcursor.getString(3),markcursor.getString(2),markcursor.getString(5)));
            readmark.add(Integer.valueOf(markcursor.getString(3)));
        }
        ReadingPageBookmarkAdapter adapter = new ReadingPageBookmarkAdapter(mData, ReadingPageActivity.this);
        readmarkview.setAdapter(adapter);
    }
    //改变屏幕亮度
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }
    //获取屏幕亮度
    private int getSystemBrightness(){
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        long endtime;
        int width = wm.getDefaultDisplay().getWidth();
        int x;
        int y;
        switch (event.getAction()) {
            /**
             * 点击的开始位置
             */
            case MotionEvent.ACTION_DOWN:
                touchstarttime = System.currentTimeMillis();
                x = (int) event.getX();
                y = (int) event.getY();
                mSelectableTextHelper.setTouchXY(x,y);
                break;
            /**
             * 触屏实时位置
             *//*
            case MotionEvent.ACTION_MOVE:
                Log.d("实时位置：",event.getX() + "+" +event.getY());
                break;
            *//**
             * 离开屏幕的位置
             */
            case MotionEvent.ACTION_UP:
                endtime = System.currentTimeMillis();
                Log.d("时间",touchstarttime+";"+endtime);
                if (endtime - touchstarttime< 500){
                    x = (int) event.getX();
                    if(x < width/2 - 200) {
                        if(curentpage>0){
                            Integer page = curentpage - 1;
                            changePage(page);
                        }
                    }
                    else if(x > width/2 + 200){
                        if(curentpage < firstpageSeeks.size() || curentpage < pageSeeks.size()){
                            Integer page = curentpage + 1;
                            changePage(page);
                        }
                    }
                    else {
                        isShowTopAndBottomLan();
                    }
                }
                break;
            /*default:
                break;*/
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cursor curentpagecusor = db.rawQuery("select * from curentpagetable where path = '" +path+ "'",null);
        if (curentpagecusor.getCount() == 0) {
            ContentValues cValuecurentpage = new ContentValues();
            cValuecurentpage.put("path", path);
            cValuecurentpage.put("curentpage", curentpage);
            db.insert("curentpagetable",null, cValuecurentpage);
        }
        else {
            db.execSQL("update curentpagetable set curentpage = "+curentpage+" where path = '" +path+"'");
        }
    }
}
