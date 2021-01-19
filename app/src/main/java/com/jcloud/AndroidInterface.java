package com.jcloud;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.jcloud.demo.QRCodeActivity;

public class AndroidInterface {
    private MainActivity activity;
    private WebView webview;
    AndroidInterface(MainActivity activity, WebView webview) {

        this.activity = activity;
        this.webview = webview;
    }

    @JavascriptInterface
    public void test(String s) {
        Toast.makeText(this.activity, s, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void scan() {
        // this.activity.scan();
        this.transferDataToWebView("from android");
    }

    @JavascriptInterface
    public void toast(String message) {
        // this.activity.scan();
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }

    public void transferDataToWebView(String result) {
        webview.evaluateJavascript("javascript:callJS()", new ValueCallback<String>(){
            @Override
           public void onReceiveValue(String s) {
                toast("s");
            }
        });
    }

}
