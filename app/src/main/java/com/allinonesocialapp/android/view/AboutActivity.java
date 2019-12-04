package com.allinonesocialapp.android.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allinonesocialapp.android.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.google_ic).setVisibility(View.GONE);
        findViewById(R.id.fb_ic).setVisibility(View.GONE);

        findViewById(R.id.pageTitle).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.pageTitle)).setText("About");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);



        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(0x2741F6);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl","https://firebasestorage.googleapis.com/v0/b/all-in-one-messenger-app.appspot.com/o/policy.html?alt=media&token=cd8e504f-ad5c-48de-b84b-07995ca99dd9");
                intent.putExtra("webName", "Privacy Policy");
                startActivity(intent);
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
