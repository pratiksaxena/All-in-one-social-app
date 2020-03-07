package messenger.people.messenger.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import messenger.people.messenger.R;

import com.calldorado.Calldorado;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //Map<Calldorado.Condition, Boolean> cdoConditions = Calldorado.getAcceptedConditions(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5550326882103592/1175028210");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);

                finish();
            }
        });

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    //if (cdoConditions.containsKey(Calldorado.Condition.EULA) && cdoConditions.get(Calldorado.Condition.EULA)) {

                        if(mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }


//                    }else{
//                        Intent intent = new Intent(getApplicationContext(), CalldoRadoActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }



                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 2000);

    }

    public static void onCdoDataReset(Context context) { //Example. Resetting firebase on CDO data reset
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.resetAnalyticsData();
    }
}
