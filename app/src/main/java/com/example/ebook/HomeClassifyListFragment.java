package com.example.ebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebook.Util.Util;
import com.example.ebook.view.MyWebView;

public class HomeClassifyListFragment extends Fragment {
    private int type;
    private MyWebView web_view;
    public static HomeClassifyListFragment newInstance(int type){
        Bundle args = new Bundle();
        HomeClassifyListFragment fragment = new HomeClassifyListFragment();
        args.putInt("type", type);
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webviewcontent, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getInt("type");
        web_view = view.findViewById(R.id.ly_webviewcontent);
        Util.initView(web_view);
        web_view.setWebViewClient(new WebViewClient());
        web_view.addJavascriptInterface(new javaToJS(),"javaToJS");
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
        switch (type){
            case 0:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=0");
                break;
            case 1:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=1");
                break;
            case 2:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=2");
                break;
            case 3:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=3");
                break;
            case 4:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=4");
                break;
            case 5:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=5");
                break;
            case 6:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=6");
                break;
            case 7:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=7");
                break;
            case 8:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=8");
                break;
            case 9:
                web_view.loadUrl(getResources().getString(R.string.url)+"/bookstore?type=9");
                break;
        }
    }
    public class javaToJS{
        @JavascriptInterface
        public void toContent(String bookid){
            Intent intent = new Intent(getActivity(), BookContentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("bookid",bookid);
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
    }
}
