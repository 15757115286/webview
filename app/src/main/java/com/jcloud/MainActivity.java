package com.jcloud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanKitActivity;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.jcloud.demo.QRCodeActivity;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQ_CODE = 111;
    public static final int DECODE = 1;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;

    private AndroidInterface bridge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为全屏（隐藏状态栏）
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        this.createWebView();
    }

    /* 创建 WebView 实例 */
    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        MainActivity activity = this;
        // 创建 WebView 实例并通过 id 绑定我们刚在布局中创建的 WebView 标签
        // 这里的 R.id.webview 就是 activity_main.xml 中的 WebView 标签的 id
        final WebView webView = (WebView) findViewById(R.id.wv);
        // 设置 WebView 允许执行 JavaScript 脚本
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        AndroidInterface bridge = new AndroidInterface(activity, webView);
        webView.addJavascriptInterface(bridge, "bridge");
        // 确保跳转到另一个网页时仍然在当前 WebView 中显示
        // 而不是调用浏览器打开
        webView.setWebChromeClient(new WebChromeClient());
        // 设置 WebView 的按键监听器，覆写监听器的 onKey 函数，对返回键作特殊处理
        // 当 WebView 可以返回到上一个页面时回到上一个页面
        webView.setOnKeyListener(new View.OnKeyListener() {
            private long exitTime = 0;
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //  按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();

                        return true;
                    }else{
                        ExitApp();
                    }
                }
                return true;
            }

            public void ExitApp() {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }

        });
        // 加载指定网页
        String url = "http://192.168.0.189:4200";
        webView.loadUrl(url);
    }

    public void scan() {
        requestPermission(CAMERA_REQ_CODE, DECODE);
    }


    //编辑请求权限
    private void requestPermission(int requestCode, int mode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }


    //权限申请返回
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return;
        }

        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == CAMERA_REQ_CODE) {
            //启动扫描Acticity
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
    }


    //Activity回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                String result = obj.originalValue;
                // Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                bridge.transferDataToWebView(result);
            }
        }
    }
}