package com.allinonesocialapp.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allinonesocialapp.android.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import adapter.SocialAppsAdapter;
import model.SocialApp;
import model.SocialAppDTO;
import util.CommonUtil;
import util.RecyclerViewMargin;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecyclerView.Adapter installedAppAdapter;
    private RecyclerView.Adapter exploreAppAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private List<SocialApp> socialAppList = new ArrayList<>();
    private List<SocialApp> allInstalledApps = new ArrayList<>();
    private ArrayList<SocialApp> installedSocialApps = new ArrayList<>();
    private List<SocialApp> absentSocialApps = new ArrayList<>();

    Map<String, SocialApp> socialMap = new HashMap<>();

    LinearLayout feedLayout;
    LinearLayout messageLayout;
    LinearLayout friendsLayout;
    LinearLayout searchLayout;
    ArrayList<SocialAppDTO> appList = new ArrayList<>();

    private ShimmerFrameLayout mShimmerViewContainer;
    Handler handler;

    Executor executor = Executors.newSingleThreadExecutor();

    //SwipeRefreshLayout swipeRefreshLayout;

    private AdView mAdView;
    private AdView mAdView2;
    boolean isPausedCalled  = false;

    boolean isBackPressed = false;
    private InterstitialAd backInterstitialAd;
    private InterstitialAd webViewInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        configureToolbar();
        setUpDrawer();

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        getDataFromFirebase();

        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 1){
                    setUpApplication((ArrayList<SocialAppDTO>) msg.obj);
                }
            }
        };

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                Log.d("LOG", initializationStatus.toString());
            }
        });


        setUpBannerAd1();
        setUpBackInterstialAds();
        setUpWebViewIntertialAds();
        setUpBottomAds();

    }

    public void setUpBannerAd1(){
        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener(){

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView.setVisibility(View.GONE);
            }

        });
    }

    /*
     * Not being used right now
     */
    public void setUpBannerAd2(){
        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest1);

        mAdView2.setAdListener(new AdListener(){

            @Override
            public void onAdLoaded() {
                mAdView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView2.setVisibility(View.GONE);
            }

        });
    }

    public void setUpWebViewIntertialAds(){

        webViewInterstitialAd = new InterstitialAd(this);
        webViewInterstitialAd.setAdUnitId("ca-app-pub-5550326882103592/8938922458");
        webViewInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    public void setUpBackInterstialAds(){

        backInterstitialAd = new InterstitialAd(this);
        backInterstitialAd.setAdUnitId("ca-app-pub-5550326882103592/4808105755");
        backInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    public void setUpBottomAds(){
        AdView mAdView = findViewById(R.id.bottomAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {

        if(!isBackPressed){
            isBackPressed = true;
            if(backInterstitialAd.isLoaded()) {
                // show ad
                backInterstitialAd.show();
                setUpBackInterstialAds();
            }else{
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            // show ad code
            if(webViewInterstitialAd.isLoaded()){
                webViewInterstitialAd.show();
                setUpWebViewIntertialAds();
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        isBackPressed = false;
        if(isPausedCalled){
            installedSocialApps = getUsageStats(installedSocialApps);
            installedAppAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPausedCalled = true;
    }

    private void setUpApplication(ArrayList<SocialAppDTO> appList) {
        this.appList = appList;
        installedSocialApps.clear();
        absentSocialApps.clear();

        getMasterAppList(appList);

        final PackageManager pm = getPackageManager();

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            SocialApp s = new SocialApp(null, pm.getApplicationIcon(packageInfo), packageInfo.loadLabel(pm).toString(), null, packageInfo.packageName, false);
            socialMap.put(s.packageName, s);
        }

        for(SocialApp socialApp : socialAppList){
            if(socialMap.get(socialApp.packageName) != null){
                SocialApp app = socialMap.get(socialApp.packageName);
                app.imageUrl = socialApp.imageUrl;
                app.isInstalled = true;
                installedSocialApps.add(app);
            }else if(socialApp.webUrl != null){
                absentSocialApps.add(socialApp);
            }
        }

        setUpInstalledAppSection(installedSocialApps);
        setUpExploreAppSection(absentSocialApps);
        setUpFacebookSection();

    }

    public List<SocialAppDTO> getDataFromFirebase(){

        final ArrayList<SocialAppDTO> appList = new ArrayList<>();


        //swipeRefreshLayout.setRefreshing(true);

        mShimmerViewContainer.startShimmer();

                FirebaseFirestore db = FirebaseFirestore.getInstance();


                db.collection("socialApps")
                        .get()

                        .addOnCompleteListener(executor, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        appList.add(CommonUtil.fromJson(CommonUtil.getJson(document.getData()), SocialAppDTO.class));
                                    }

                                    Message uisetup = new Message();
                                    uisetup.what = 1;
                                    uisetup.obj = appList;
                                    handler.dispatchMessage(uisetup);
                                    //setUpApplication(appList);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

//                                            swipeRefreshLayout.post(new Runnable() {
//                                                @Override
//                                                public void run() { swipeRefreshLayout.setRefreshing(false);
//
//                                                }
//                                            });

                                            //swipeRefreshLayout.setEnabled(false);
                                            mShimmerViewContainer.stopShimmer();
                                            mShimmerViewContainer.setVisibility(View.GONE);
                                            findViewById(R.id.segment_one).setVisibility(View.VISIBLE);
                                            findViewById(R.id.segment_two).setVisibility(View.VISIBLE);
                                            findViewById(R.id.segment_three).setVisibility(View.VISIBLE);

                                        }
                                    });


                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




        return appList;

    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_action_menu_white);
        actionbar.setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.google_ic).setVisibility(View.VISIBLE);
        findViewById(R.id.fb_ic).setVisibility(View.VISIBLE);
        findViewById(R.id.google_ic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl","https://google.com");
                startActivity(intent);
            }
        });

        findViewById(R.id.fb_ic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl","https://fb.com");
                startActivity(intent);
            }
        });
    }

    private void getMasterAppList(List<SocialAppDTO> appList){

        socialAppList.clear();

        for(SocialAppDTO app : appList){
            socialAppList.add(new SocialApp(app.imageUrl, null, app.appName, app.webUrl, app.packageName, app.isAppOnly));
        }

    }

    private void setUpInstalledAppSection(ArrayList<SocialApp> apps) {

        if(apps == null || apps.size() == 0){
            findViewById(R.id.segment_one).setVisibility(View.GONE);
        }

//        if(apps.size() > 8){
//            List<SocialApp> subList = apps.subList(0, 8);
//            apps.clear();
//            apps.addAll(subList);
//        }

        apps = getUsageStats(apps);

        recyclerView = (RecyclerView) findViewById(R.id.app_container);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 4);

        installedAppAdapter = new SocialAppsAdapter(apps, getBaseContext());

        recyclerView.setAdapter(installedAppAdapter);
        recyclerView.addItemDecoration(new RecyclerViewMargin(50, 4));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        findViewById(R.id.usageAnalysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),  UsageStatsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appList", appList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private ArrayList<SocialApp> getUsageStats(ArrayList<SocialApp> apps) {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        if(mode == AppOpsManager.MODE_ALLOWED){

            Map<String, UsageStats> statsMap = new HashMap<>();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            long start = calendar.getTimeInMillis();
            long end = System.currentTimeMillis();

            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

            List<UsageStats> usagestats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);

            for(UsageStats stats : usagestats){
                statsMap.put(stats.getPackageName(), stats);
            }

            apps = getInstalledAppList(apps, statsMap);

        }
        return apps;
    }

    private ArrayList<SocialApp> getInstalledAppList(ArrayList<SocialApp> appList, Map<String, UsageStats> stats){

        if(appList == null || appList.size() == 0){
            return null;
        }

        final PackageManager pm = getPackageManager();

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            SocialApp s = new SocialApp(null, pm.getApplicationIcon(packageInfo), packageInfo.loadLabel(pm).toString(), null, packageInfo.packageName, false);
            socialMap.put(s.packageName, s);
        }

        for(SocialApp socialApp : appList){

            if(socialMap.get(socialApp.packageName) != null){

                if(stats != null && stats.containsKey(socialApp.packageName)){
                    try{
                        Field mLaunchCount = UsageStats.class.getDeclaredField("mLaunchCount");
                        Field mTotalTimeInForeground = UsageStats.class.getDeclaredField("mTotalTimeInForeground");
                        int launchCount = (Integer)mLaunchCount.get(stats.get(socialApp.packageName));
                        long usageTime = (Long)mTotalTimeInForeground.get(stats.get(socialApp.packageName));
                        socialApp.useCount = launchCount;
                        socialApp.duration = usageTime;
                    }catch (Exception e){
                        Log.d("error", e.getMessage());
                    }

                }

            }
        }

        return appList;

    }

    private void setUpExploreAppSection(List<SocialApp> apps) {

        if(apps == null || apps.size() == 0){
            findViewById(R.id.segment_two).setVisibility(View.GONE);
        }

//        if(apps.size() > 8){
//            List<SocialApp> subList = apps.subList(0, 8);
//            apps.clear();
//            apps.addAll(subList);
//        }

        recyclerView1 = (RecyclerView) findViewById(R.id.app_container_1);
        recyclerView1.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 4);

        exploreAppAdapter = new SocialAppsAdapter(apps, this);

        recyclerView1.setAdapter(exploreAppAdapter);
        recyclerView1.addItemDecoration(new RecyclerViewMargin(50, 4));
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setNestedScrollingEnabled(false);
    }

    private void setUpDrawer() {
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name, R.string.app_name);
        t.getDrawerArrowDrawable().setColor(Color.BLACK);
        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.usageAnalysis:
                        Intent intent = new Intent(getBaseContext(),  UsageStatsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", appList);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.share:
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Download the Text Messenger App app for Free text messages & Video chat https://play.google.com/store/apps/details?id=com.allinonesocialapp.android");
                        try {
                            getBaseContext().startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {

                        }
                        break;
                    case R.id.rate:
                        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
                        }
                        break;
                    case R.id.about:
                        Intent Aboutintent = new Intent(getBaseContext(), AboutActivity.class);
                        startActivity(Aboutintent);
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    void setUpFacebookSection(){

        feedLayout = (LinearLayout)findViewById(R.id.feed);
        messageLayout = (LinearLayout)findViewById(R.id.message);

        feedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPagerActivity(1);
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://m.facebook.com/home.php");
                intent.putExtra("webName", "Facebook");
                startActivity(intent);
            }
        });

        messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPagerActivity(2);
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://www.messenger.com/login/");
                intent.putExtra("webName", "Facebook");
                startActivity(intent);
            }
        });

//        friendsLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPagerActivity(3);
//            }
//        });
//
//        searchLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPagerActivity(4);
//            }
//        });

    }

    void openPagerActivity(int pageId){
        Intent intent = new Intent(this, PagerActivity.class);
        intent.putExtra("pageId", String.valueOf(pageId));
        startActivity(intent);
    }

}

