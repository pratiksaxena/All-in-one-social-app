package messenger.people.messenger.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import messenger.people.messenger.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String appName = getIntent().getStringExtra("webName");
        setUpToolBar(appName);

        ProgressBar progressBar = findViewById(R.id.progress);

        progressBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(getBaseContext(),R.color.app_primary), android.graphics.PorterDuff.Mode.SRC_IN);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebChromeClient(new AppWebViewClient(progressBar));
        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(getIntent().getStringExtra("webUrl"));
    }

    public void setUpToolBar(String appName){
        findViewById(R.id.google_ic).setVisibility(View.GONE);
        findViewById(R.id.fb_ic).setVisibility(View.GONE);
        findViewById(R.id.pageTitle).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.pageTitle)).setText(appName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);

        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if(taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            super.onBackPressed();
        }
    }

    public class AppWebViewClient extends WebChromeClient {
        private ProgressBar progressBar;

        public AppWebViewClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(10000);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), newProgress * 100);
            animation.setDuration(500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

            //progressBar.setProgress(newProgress);
            if(newProgress == 100){
                progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);

        }

    }
}
