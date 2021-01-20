package com.jcloud;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.jcloud.demo.QRCodeActivity;

public class JSBridgeInterface {
    private MainActivity activity;
    private WebView webview;
    public final static int CODE_SUCCESS = 0;
    public final static int CODE_FAIL = -1;
    public final static int CODE_CANCEL_SCAN = -2;
    JSBridgeInterface(MainActivity activity, WebView webview) {

        this.activity = activity;
        this.webview = webview;
    }

    @JavascriptInterface
    public void test(String s) {
        Toast.makeText(this.activity, s, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void scan() {
        this.activity.scan();
    }

    @JavascriptInterface
    public void toast(String message) {
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }

    public void transferDataToWebView(String type, int code, String result) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String script = "javascript:__android_js_bridge_channel('%s', %d, '%s')";
                webview.evaluateJavascript(String.format(script, type, code, result), new ValueCallback<String>(){
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
            }
        });
    }

}
