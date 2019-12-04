package com.allinonesocialapp.android.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.allinonesocialapp.android.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.UsageAppAdapter;
import model.SocialApp;
import model.SocialAppDTO;
import util.RecyclerViewMargin;

public class UsageStatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Map<String, SocialApp> socialMap = new HashMap<>();
    List<SocialAppDTO> appList = new ArrayList<>();

    List<SocialApp> socialAppList = new ArrayList<SocialApp>();

    boolean isRedirected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_stats);
        setUpToolBar();

        // Get list of apps from intent
        appList = (List<SocialAppDTO>) getIntent().getExtras().getSerializable("appList");

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        if(mode == AppOpsManager.MODE_ALLOWED){

            recyclerView = (RecyclerView) findViewById(R.id.usage_container);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            adapter = new UsageAppAdapter(socialAppList);

            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new RecyclerViewMargin(10, 4));
            recyclerView.setLayoutManager(layoutManager);

            updateSpinnerSelection("Daily");


        }else if(!isRedirected) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 101);
            isRedirected = true;
        }
    }

    public void updateSpinnerSelection(String selection){

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        List<UsageStats> usagestats = new ArrayList<>();

        Map<String, UsageStats> statsMap = new HashMap<>();

        switch(selection){
            case "Daily":
                usagestats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                break;
            case "Weekly":
                usagestats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, start, end);
                break;
            case "Monthly":
                usagestats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, start, end);
                break;
            case "Best":
                statsMap = usageStatsManager.queryAndAggregateUsageStats(start, end);

                break;
        }

        for(UsageStats stats : usagestats){
            statsMap.put(stats.getPackageName(), stats);
        }

        List<SocialApp> apps = getInstalledAppList(appList, statsMap);
        socialAppList.removeAll(socialAppList);
        socialAppList.addAll(apps);
        adapter.notifyDataSetChanged();

    }


    private List<SocialApp> getInstalledAppList(List<SocialAppDTO> appList, Map<String, UsageStats> stats){

        if(appList == null || appList.size() == 0){
            return null;
        }

        final PackageManager pm = getPackageManager();

        List<SocialApp> installedSocialApp = new ArrayList<SocialApp>();

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            SocialApp s = new SocialApp(null, pm.getApplicationIcon(packageInfo), packageInfo.loadLabel(pm).toString(), null, packageInfo.packageName, false);
            socialMap.put(s.packageName, s);
        }

        for(SocialAppDTO socialApp : appList){

            int count =0 ;

            if(socialMap.get(socialApp.packageName) != null){

                if(stats != null && stats.containsKey(socialApp.packageName)){
                    try{
                        Field mLaunchCount = UsageStats.class.getDeclaredField("mLaunchCount");
                        Field mTotalTimeInForeground = UsageStats.class.getDeclaredField("mTotalTimeInForeground");
                        int launchCount = (Integer)mLaunchCount.get(stats.get(socialApp.packageName));
                        long usageTime = (Long)mTotalTimeInForeground.get(stats.get(socialApp.packageName));
                        SocialApp app = socialMap.get(socialApp.packageName);
                        app.useCount = launchCount;
                        count = launchCount;
                        app.duration = usageTime;
                    }catch (Exception e){
                        Log.d("error", e.getMessage());
                    }

                }

                if(count == 0){
                    continue;
                }

                installedSocialApp.add(socialMap.get(socialApp.packageName));
            }
        }

        return installedSocialApp;

    }

    public void setUpToolBar(){
        findViewById(R.id.google_ic).setVisibility(View.GONE);
        findViewById(R.id.fb_ic).setVisibility(View.GONE);
        findViewById(R.id.pageTitle).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.pageTitle)).setText("App Usage Analysis");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUsage);

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

//        Spinner spinner = (Spinner) findViewById(R.id.usage_spinner);
//        findViewById(R.id.usage_spinner).setVisibility(View.VISIBLE);
//
//        List<String> list = new ArrayList<>();
//        list.add("Daily");
//        list.add("Weekly");
//
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spinner.setAdapter(dataAdapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String seletedItem = parent.getItemAtPosition(position).toString();
//                updateSpinnerSelection(seletedItem);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


    }




}
