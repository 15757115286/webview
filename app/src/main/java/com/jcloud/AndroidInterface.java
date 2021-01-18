package com.jcloud;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class AndroidInterface {
    private Context context;
    AndroidInterface(Context context) {
        this.context = context;
    }
    @JavascriptInterface
    public void test(String name){
        Toast.makeText(context ,name,Toast.LENGTH_SHORT).show();
    }
}
