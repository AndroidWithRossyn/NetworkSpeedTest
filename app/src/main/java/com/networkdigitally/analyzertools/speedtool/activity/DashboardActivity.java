package com.networkdigitally.analyzertools.speedtool.activity;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.networkdigitally.analyzertools.speedtool.BuildConfig;
import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.notifications.WeatherNotificationService;
import com.pesonal.adsdk.AppManage;

public class DashboardActivity extends AppCompatActivity {

    RelativeLayout more_apps_btn, rate_us_btn, privacy_policy_btn, terms_cond_btn;
    DrawerLayout draw_lay;
    ImageView menu_btn, img1, img2, img3, img4, img5, img6;
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        menu_btn = findViewById(R.id.menu_btn);
        more_apps_btn = findViewById(R.id.more_apps_btn);
        rate_us_btn = findViewById(R.id.rate_us_btn);
        privacy_policy_btn = findViewById(R.id.privacy_policy_btn);
        draw_lay = findViewById(R.id.draw_lay);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);
        final Handler handler = new Handler();
        AppManage.getInstance(DashboardActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        AppManage.getInstance(DashboardActivity.this).loadInterstitialAd(this);
        final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1500);
            }
        };
        handler.post(runnableCode);
        img1.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(DashboardActivity.this, InternetSpeedTestActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        img2.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(DashboardActivity.this, IPAddressTestActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        img3.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(DashboardActivity.this, SoundTestActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        img4.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(DashboardActivity.this, VehicleSpeedTestActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        img5.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(DashboardActivity.this, MapNavigationActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        img6.setOnClickListener(v -> AppManage.getInstance(DashboardActivity.this).showInterstitialAd(DashboardActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                showWeatherNotificationIfNeeded();
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd));
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw_lay.openDrawer(Gravity.RIGHT);
            }
        });
        more_apps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "WA GB Tools 2022");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });
        rate_us_btn.setOnClickListener(v -> {
            draw_lay.closeDrawers();
            launchMarket();
        });
        privacy_policy_btn.setOnClickListener(v -> {
            draw_lay.closeDrawers();
            gotoUrl("https://github.com/OmaPrakash");
        });
    }
    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void showWeatherNotificationIfNeeded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs == null)
            return;
        boolean foregroundServicesPermissionGranted = isForegroundServicesPermissionGranted();
        boolean isWeatherNotificationEnabled =
                prefs.getBoolean(getString(R.string.settings_enable_notification_key), false);
        if (isWeatherNotificationEnabled && foregroundServicesPermissionGranted) {
            WeatherNotificationService.start(this);
        }
    }

    private boolean isForegroundServicesPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            return true;
        return ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public void exitApp() {

        Intent intent = new Intent(DashboardActivity.this, ExitActivity.class);
        startActivity(intent);

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}