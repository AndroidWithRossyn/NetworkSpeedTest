package com.networkdigitally.analyzertools.speedtool.activity;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.networkdigitally.analyzertools.speedtool.GPSCallBack;
import com.networkdigitally.analyzertools.speedtool.GPSManager;
import com.networkdigitally.analyzertools.speedtool.R;
import com.pesonal.adsdk.AppManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import in.unicodelabs.kdgaugeview.KdGaugeView;

public class VehicleSpeedTestActivity extends AppCompatActivity implements GPSCallBack {
    private GPSManager gpsManager = null;
    public static float finalsl;
    private double speed = 0.0;
    Boolean isGPSEnabled = false;
    LocationManager locationManager;
    double currentSpeed, kmphSpeed;
    TextView txtview;
    KdGaugeView speedoMeterView;
//    KdGaugeView speedLimitMeter;
    float SPEED_LIMIT = 60f;
    double latitude, longitude;
//    RelativeLayout gradient;
    LocationListener locationListener;
    private RequestQueue mQueue;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    public double clat;
    public double clong;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_speed_test);
        AppManage.getInstance(VehicleSpeedTestActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        back = findViewById(R.id.back);
        checkLocationisEnabledOrNot();
//        gradient = findViewById(R.id.gradient);
//        AnimationDrawable animationDrawable = (AnimationDrawable) gradient.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleSpeedTestActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
        txtview = findViewById(R.id.info);
        speedoMeterView = findViewById(R.id.speedMeter);
//        speedLimitMeter = findViewById(R.id.speedLimitMeter);
        mQueue = Volley.newRequestQueue(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getCurrentSpeed();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double temp1 = location.getLatitude();
                double temp2 = location.getLongitude();
                clat = Double.parseDouble(df2.format(temp1));
                clong = Double.parseDouble(df2.format(temp2));
                jsonParse();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void getCurrentSpeed() {
        txtview.setText(getString(R.string.info));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(VehicleSpeedTestActivity.this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            gpsManager.startListening(getApplicationContext());
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }

    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
        txtview.setText(kmphSpeed + "km/h");
        txtview.setText("");
        if (kmphSpeed > SPEED_LIMIT) {
            MediaPlayer mediaPlayer = new MediaPlayer().create(getApplicationContext(), R.raw.william);
            mediaPlayer.start();
        }
        speedoMeterView.setSpeed((float) kmphSpeed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(VehicleSpeedTestActivity.this, DashboardActivity.class);
        startActivity(intent);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putFloat("KMPH", (float) kmphSpeed);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        float Speed = savedInstanceState.getFloat("KMPH", 0.0f);
        txtview.setText(Speed + "km/h");
        speedoMeterView.setSpeed(Speed);
        txtview.setText(getString(R.string.info));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(VehicleSpeedTestActivity.this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            gpsManager.startListening(getApplicationContext());
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }
    private void checkLocationisEnabledOrNot() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = false;
        boolean gpsEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gpsEnabled && !networkEnabled) {
            new AlertDialog.Builder(VehicleSpeedTestActivity.this).setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel", null).show();
        }
    }

    private void jsonParse() {
        String url = "https://api.myjson.com/bins/djbk6";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("SpeedLimits");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject s = jsonArray.getJSONObject(i);
                                double lat = s.getDouble("latitude");
                                double longi = s.getDouble("longitude");
                                int speedl = s.getInt("speedlimit");
//                                if (lat == clat && longi == clong) {
//                                    finalsl = speedl;
                                    //  Toast.makeText(MainActivity.this,"hiii"+finalsl, Toast.LENGTH_SHORT).show();
//                                    speedLimitMeter.setSpeed(finalsl);
//                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}