package oas.iot.unipr.it.iotsecureclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oas.iot.unipr.it.iotsecureclient.Model.ConsumerInfo;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Remote.ConsumerInfoGetter;
import oas.iot.unipr.it.iotsecureclient.Remote.IResourceGetter;


public class LoginActivity extends ActionBarActivity
implements View.OnClickListener{

    private static final String AUTH_URL = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/oauth";
    private static final String AUTH_CALLBACK_URL = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/oauth2callback";

    private AlertDialog dialog;
    private ActionBar myActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar setup
        Toolbar toolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        myActionBar = getSupportActionBar();
        myActionBar.setHomeButtonEnabled(false);
        myActionBar.setDisplayHomeAsUpEnabled(false);
        myActionBar.setTitle(getResources().getString(R.string.login_title));
        //View listeners set
        findViewById(R.id.facebook_login_button).setOnClickListener(this);
        findViewById(R.id.google_login_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.facebook_login_button){
            callWebView("/facebook");
        }else if (v.getId()==R.id.google_login_button){
            callWebView("/google");
        }
    }

    public void callWebView(String uri){
        WebView wv = new WebView(this);
        wv.loadUrl(AUTH_URL + uri);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith(AUTH_CALLBACK_URL)){
                    Log.d("WebView", "Oauth completed");
                    //Start main activity
                    if (dialog!=null)dialog.dismiss();
                    ConsumerInfoGetter getter = new ConsumerInfoGetter();
                    getter.requestResourceFromUrl(url, new IResourceGetter.IResourceReady<ConsumerInfo>() {
                        @Override
                        public void objectReceived(ConsumerInfo result) {
                            InfoManager.getInstance().setMyInfo(result);
                            //Open main activity
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
                }else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(wv);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }
}

