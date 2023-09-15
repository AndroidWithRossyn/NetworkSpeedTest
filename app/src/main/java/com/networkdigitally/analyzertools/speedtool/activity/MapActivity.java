package com.networkdigitally.analyzertools.speedtool.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.utils.UI;
import com.networkdigitally.analyzertools.speedtool.viewmodels.MapViewModel;
import com.networkdigitally.analyzertools.speedtool.weatherapi.WeatherStorage;
import com.pesonal.adsdk.AppManage;

public class MapActivity extends BaseActivity {

    private WebView webView;
    private MapViewModel mapViewModel;
    private WeatherStorage weatherStorage;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        AppManage.getInstance(MapActivity.this).loadInterstitialAd(this);

        //noinspection ConstantConditions
        setTheme(theme = UI.getTheme(prefs.getString("theme", "fresh")));
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        weatherStorage = new WeatherStorage(this);

        if (savedInstanceState == null) {
            mapViewModel.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mapViewModel.mapLat = weatherStorage.getLatitude(0);
            mapViewModel.mapLon = weatherStorage.getLongitude(0);
            mapViewModel.apiKey = mapViewModel.sharedPreferences.getString("apiKey", getResources().getString(R.string.apiKey));
        }

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map.html?lat=" + mapViewModel.mapLat + "&lon="
                + mapViewModel.mapLon + "&appid=" + mapViewModel.apiKey
                + "&zoom=" + mapViewModel.mapZoom + "&displayPin=true");
        webView.addJavascriptInterface(new HybridInterface(), "NativeInterface");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (savedInstanceState != null) {
                    setMapState(mapViewModel.tabPosition);
                }
            }
        });

        BottomNavigationView bottomBar = findViewById(R.id.navigationBar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();
                setMapState(i);
                mapViewModel.tabPosition = i;
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        AppManage.getInstance(MapActivity.this).showInterstitialAd(MapActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                MapActivity.super.onBackPressed();
            }
        },"",AppManage.app_mainClickCntSwAd);

    }

    private void setMapState(int item) {
        switch (item) {
            case R.id.map_clouds:
                webView.loadUrl("javascript:map.removeLayer(rainLayer);map.removeLayer(windLayer);map.removeLayer(tempLayer);"
                        + "map.addLayer(cloudsLayer);");
                break;
            case R.id.map_rain:
                webView.loadUrl("javascript:map.removeLayer(cloudsLayer);map.removeLayer(windLayer);map.removeLayer(tempLayer);"
                        + "map.addLayer(rainLayer);");
                break;
            case R.id.map_wind:
                webView.loadUrl("javascript:map.removeLayer(cloudsLayer);map.removeLayer(rainLayer);map.removeLayer(tempLayer);"
                        + "map.addLayer(windLayer);");
                break;
            case R.id.map_temperature:
                webView.loadUrl("javascript:map.removeLayer(cloudsLayer);map.removeLayer(windLayer);map.removeLayer(rainLayer);"
                        + "map.addLayer(tempLayer);");
                break;
            default:
                Log.w("MapActivity", "Layer not configured");
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class HybridInterface {

        @JavascriptInterface
        public void transferLatLon(double lat, double lon) {
            mapViewModel.mapLat = lat;
            mapViewModel.mapLon = lon;
        }

        @JavascriptInterface
        public void transferZoom(int level) {
            mapViewModel.mapZoom = level;
        }
    }

}
