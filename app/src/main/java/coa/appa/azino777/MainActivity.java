package coa.appa.azino777;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appsflyer.AppsFlyerLib;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WebView magicview;
    WebSettings settingparam;
    String subs = "";
    String[] parts;
    String name = "";
    String urlLoads = "";
    static NetworkInfo netInfo;
    private String urlhosting = "";
    public String gaid;
    private String app_name = "coa.appa.azino777-Azino";
    private static final String app_key = "fVjUwR8kk6ABDEgfhwUSqh";
    private static final String signal_one = "d90c89ea-ac16-4c7e-8c70-53fb9052a36a";
    String urlfirsting = "";
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "myurl";
    public static final String prefer_Url = "url";
    SharedPreferences.Editor editor;
    //
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings.contains(prefer_Url)) {
            urlfirsting = mSettings.getString(prefer_Url, "");
        }

        String campaign = MyApp.getCampaignFromAssets(this);

        if (campaign != null) {
            Log.d("MainActivity", "Кампания получена: " + campaign);
            parts = campaign.split("_");
        } else {
            Log.d("MainActivity", "Кампания не найдена");
        }
        manuallySendInstallToAppsFlyer(campaign);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(signal_one);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        try {
            urlhosting = data.getQuery();
            urlhosting = "&" + urlhosting;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Foam();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void Foam()
    {
        MyApp.fetchGAID(gaid -> {
            final String idnorm = AppsFlyerLib.getInstance().getAppsFlyerUID(this);
            this.gaid = gaid;
            if (parts == null) {
                subs = "";
                urlLoads = "https://brazino.website/yJqzvLtd" + subs;
                if (urlhosting.equals("?"))
                    urlLoads = urlLoads + "/?afid=" + idnorm + "&appid=" + app_name + "&afToken=" + app_key + "&gaid=" + gaid + urlhosting;
                else urlLoads = urlLoads + "/?afid=" + idnorm + "&appid=" + app_name + "&afToken=" + app_key + "&gaid=" + gaid;
            } else {
                subs = "/?sub1=" + parts[0] + "&sub2=" + parts[1] + "&sub3=" + parts[2] + "&sub4=" + parts[3] + "&sub5=" + parts[4] + "&sub6=" + parts[5];
                urlLoads = "https://brazino.website/yJqzvLtd" + subs;
                if (urlhosting.equals("?"))
                    urlLoads = urlLoads + "&afid=" + idnorm + "&appid=" + app_name + "&afToken=" + app_key + "&gaid=" + gaid + urlhosting;
                else urlLoads = urlLoads + "&afid=" + idnorm + "&appid=" + app_name + "&afToken=" + app_key + "&gaid=" + gaid;
            }

            Log.d("LOADURL", urlLoads);
            magicview = findViewById(R.id.viewmagic);
            magicview.post(new Runnable() {
                @Override
                public void run() {
                    settingparam = magicview.getSettings();
                    settingparam.setJavaScriptEnabled(true);
                    settingparam.setDomStorageEnabled(true);
                    settingparam.setBuiltInZoomControls(true);
                    settingparam.setSupportZoom(false);
                    settingparam.setDisplayZoomControls(false);
                    magicview.setInitialScale(1);
                    magicview.getSettings().setLoadWithOverviewMode(true);
                    magicview.getSettings().setUseWideViewPort(true);
                    magicview.setWebChromeClient(new MyWebChromeClient());
                    magicview.getSettings().setAllowContentAccess(true);
                    magicview.getSettings().setAllowFileAccess(true);
                    magicview.loadUrl(urlLoads);
                    magicview.setWebViewClient(new WebViewClient() {

                        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {

                            return false;
                        }

                        public void onPageFinished(WebView view, String url) {
                            view.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                            CookieSyncManager.getInstance().sync();
                            urlfirsting = magicview.getUrl();
                            editor = mSettings.edit();
                            editor.putString(prefer_Url, urlfirsting);
                            editor.apply();
                        }
                    });
                }
            });
            return null;
        });
    }

    @Override
    public void onBackPressed() {
        if (magicview.canGoBack()) {
            magicview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    class MyWebChromeClient extends WebChromeClient {
        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(MainActivity.this, "No open file", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null)
                return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            uploadMessage = null;
        }
    }

    public void manuallySendInstallToAppsFlyer(String campaign) {
        Map<String, Object> installData = new HashMap<>();
        installData.put("pid", "test");  // Источник установки
        installData.put("c", campaign); // Кампания
        installData.put("af_channel", "direct_apk"); // Канал установки
        installData.put("af_status", "Non-organic"); // Отмечаем как рекламную установку

        // Устанавливаем дополнительные данные
        AppsFlyerLib.getInstance().setAdditionalData(installData);

        // Отправляем событие установки
        AppsFlyerLib.getInstance().logEvent(this, "af_manual_install", installData);

        Log.d("AppsFlyer", "Ручная установка отправлена с кампанией: " + campaign);
    }

}
