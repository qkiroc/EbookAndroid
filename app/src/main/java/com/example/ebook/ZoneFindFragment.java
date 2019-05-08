package com.example.ebook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.ebook.view.MyWebView;

@SuppressLint("ValidFragment")
public class ZoneFindFragment extends Fragment {
    private View contentView;
    private MyWebView web_view;
    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.webviewcontent, container,false);
        web_view = (MyWebView) contentView.findViewById(R.id.ly_webviewcontent);
        Util.initView(web_view);
        web_view.loadUrl(getResources().getString(R.string.url)+"/zone");
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(),"javaToJS");
        web_view.setOnScrollChangeListener(new MyWebView.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

            }
        });
        return contentView;
    }
    public class javaToJS{
        @JavascriptInterface
        public void toContent(String idealid){
            Intent intent = new Intent(getActivity(), IdealContentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("idealid",idealid);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        @JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public String getUserid(){
            return Util.getUserid(String.valueOf(getActivity().getFilesDir()));
        }
        @JavascriptInterface
        public String getType(){
            return "all";
        }
    }
}
