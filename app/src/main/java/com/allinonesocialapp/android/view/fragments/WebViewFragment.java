package com.allinonesocialapp.android.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.allinonesocialapp.android.R;

public class WebViewFragment extends Fragment {

    int pageId = 1;

    public WebViewFragment(int pageId) {
        this.pageId = pageId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView myWebView = (WebView) view.findViewById(R.id.fragment_webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(getPageUrlByPageView(pageId));
        return view;
    }

    private String getPageUrlByPageView(int pageId){

        String url = null;

        if(pageId == 2){
            url = "https://m.facebook.com/messages/?_rdr";
        }else if (pageId == 3){
            url  = "https://m.facebook.com/friends/center/requests/?rfj&no_hist=1";
        }else if (pageId == 4){
            url  = "https://m.facebook.com/home.php?soft=search";
        }else {
            url  = "https://m.facebook.com/home.php";
        }

        return url;

    }


}
