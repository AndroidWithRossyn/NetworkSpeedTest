package com.networkdigitally.analyzertools.speedtool.activity;
import static android.Manifest.permission.READ_PHONE_STATE;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.test.HttpDownloadTest;
import com.networkdigitally.analyzertools.speedtool.test.HttpUploadTest;
import com.networkdigitally.analyzertools.speedtool.test.PingTest;
import com.pesonal.adsdk.AppManage;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
public class InternetSpeedTestActivity extends AppCompatActivity implements LocationListener {
    static int position = 0;
    static int lastPosition = 0;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    TextView tv_loc1, tv_loc2, sim, get_ip;
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;
    TelephonyManager telephonyManager;
    @Override
    public void onResume() {
        super.onResume();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }
    ImageView back;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_speed_test);
        getIpAddress();
        back = findViewById(R.id.back);
        tv_loc1 = findViewById(R.id.tv_loc1);
        tv_loc2 = findViewById(R.id.tv_loc2);
        get_ip = findViewById(R.id.get_ip);
        sim = findViewById(R.id.sim);
        checkLocationisEnabledOrNot();
        getLocation();
        getIpAddress();
        getServer();
        AppManage.getInstance(InternetSpeedTestActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        get_ip.setText(ipAddress);
        get_ip.setText(getIpAdddress());
//        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInf = wifiMan.getConnectionInfo();
//        int ipAdddress = wifiInf.getIpAddress();
//        ipAddress = String.format("%d.%d.%d.%d", (ipAdddress & 0xff), (ipAdddress >> 8 & 0xff), (ipAdddress >> 16 & 0xff), (ipAdddress >> 24 & 0xff));
//        get_ip.append(ipAddress);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManage.getInstance(InternetSpeedTestActivity.this).showInterstitialAd(InternetSpeedTestActivity.this, new AppManage.MyCallback() {
                    public void callbackCall() {
                        Intent intent = new Intent(InternetSpeedTestActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }

                }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);

            }
        });
        final Button startButton = (Button) findViewById(R.id.startButton);
        final DecimalFormat dec = new DecimalFormat("#.##");
        startButton.setText("Begin Test");
        tempBlackList = new HashSet<>();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManage.getInstance(InternetSpeedTestActivity.this).showInterstitialAd(InternetSpeedTestActivity.this, new AppManage.MyCallback() {
                    public void callbackCall() {
                        startButton.setEnabled(false);
                        if (getSpeedTestHostsHandler == null) {
                            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
                            getSpeedTestHostsHandler.start();
                        }
                        new Thread(new Runnable() {
                            RotateAnimation rotate;
                            ImageView barImageView = (ImageView) findViewById(R.id.barImageView);
                            TextView downloadTextView = (TextView) findViewById(R.id.downloadTextView);
                            TextView uploadTextView = (TextView) findViewById(R.id.uploadTextView);
                            @Override
                            public void run() {
                                runOnUiThread(() -> startButton.setText("Selecting best server based on ping..."));
                                int timeCount = 600;
                                while (!getSpeedTestHostsHandler.isFinished()) {
                                    timeCount--;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                    }
                                    if (timeCount <= 0) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "No Connection...", Toast.LENGTH_LONG).show();
                                            startButton.setEnabled(true);
                                            startButton.setTextSize(16);
                                            startButton.setText("Restart Test");
                                        });
                                        getSpeedTestHostsHandler = null;
                                        return;
                                    }
                                }
                                HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
                                HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
                                double selfLat = getSpeedTestHostsHandler.getSelfLat();
                                double selfLon = getSpeedTestHostsHandler.getSelfLon();
                                double tmp = 19349458;
                                double dist = 0.0;
                                int findServerIndex = 0;
                                for (int index : mapKey.keySet()) {
                                    if (tempBlackList.contains(mapValue.get(index).get(5))) {
                                        continue;
                                    }
                                    Location source = new Location("Source");
                                    source.setLatitude(selfLat);
                                    source.setLongitude(selfLon);
                                    List<String> ls = mapValue.get(index);
                                    Location dest = new Location("Dest");
                                    dest.setLatitude(Double.parseDouble(ls.get(0)));
                                    dest.setLongitude(Double.parseDouble(ls.get(1)));
                                    double distance = source.distanceTo(dest);
                                    if (tmp > distance) {
                                        tmp = distance;
                                        dist = distance;
                                        findServerIndex = index;
                                    }
                                }
                                String testAddr = mapKey.get(findServerIndex).replace("http://", "https://");
                                final List<String> info = mapValue.get(findServerIndex);
                                final double distance = dist;

                                if (info == null) {
                                    runOnUiThread(() -> {
                                        startButton.setTextSize(12);
                                        startButton.setText("There was a problem in getting Host Location. Try again later.");
                                    });
                                    return;
                                }
                                runOnUiThread(() -> {
                                    startButton.setTextSize(13);
                                    startButton.setText(String.format("Host Location: %s [Distance: %s km]", info.get(2), new DecimalFormat("#.##").format(distance / 1000)));
                                });
                                runOnUiThread(() -> {
                                    downloadTextView.setText("0 Mbps");
                                    uploadTextView.setText("0 Mbps");
                                });
                                final List<Double> pingRateList = new ArrayList<>();
                                final List<Double> downloadRateList = new ArrayList<>();
                                final List<Double> uploadRateList = new ArrayList<>();
                                Boolean pingTestStarted = false;
                                Boolean pingTestFinished = false;
                                Boolean downloadTestStarted = false;
                                Boolean downloadTestFinished = false;
                                Boolean uploadTestStarted = false;
                                Boolean uploadTestFinished = false;
                                final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 3);
                                final HttpDownloadTest downloadTest = new HttpDownloadTest(testAddr.replace(testAddr.split("/")[testAddr.split("/").length - 1], ""));
                                final HttpUploadTest uploadTest = new HttpUploadTest(testAddr);
                                while (true) {
                                    if (!pingTestStarted) {
                                        pingTest.start();
                                        pingTestStarted = true;
                                    }
                                    if (pingTestFinished && !downloadTestStarted) {
                                        downloadTest.start();
                                        downloadTestStarted = true;
                                    }
                                    if (downloadTestFinished && !uploadTestStarted) {
                                        uploadTest.start();
                                        uploadTestStarted = true;
                                    }
                                    if (pingTestFinished) {
                                        if (pingTest.getAvgRtt() == 0) {
                                            System.out.println("Ping error...");
                                        } else {
                                            runOnUiThread(() -> {
                                            });
                                        }
                                    } else {
                                        pingRateList.add(pingTest.getInstantRtt());

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });
                                        runOnUiThread(() -> {
                                            XYSeries pingSeries = new XYSeries("");
                                            pingSeries.setTitle("");

                                            int count = 0;
                                            List<Double> tmpLs = new ArrayList<>(pingRateList);
                                            for (Double val : tmpLs) {
                                                pingSeries.add(count++, val);
                                            }

                                            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                            dataset.addSeries(pingSeries);
                                        });
                                    }
                                    if (pingTestFinished) {
                                        if (downloadTestFinished) {
                                            if (downloadTest.getFinalDownloadRate() == 0) {
                                                System.out.println("Download error...");
                                            } else {
                                                runOnUiThread(() -> downloadTextView.setText(dec.format(downloadTest.getFinalDownloadRate()) + " Mbps"));
                                            }
                                        } else {
                                            double downloadRate = downloadTest.getInstantDownloadRate();
                                            downloadRateList.add(downloadRate);
                                            position = getPositionByRate(downloadRate);

                                            runOnUiThread(() -> {
                                                rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                rotate.setInterpolator(new LinearInterpolator());
                                                rotate.setDuration(100);
                                                barImageView.startAnimation(rotate);
                                                downloadTextView.setText(dec.format(downloadTest.getInstantDownloadRate()) + " Mbps");
                                            });
                                            lastPosition = position;
                                            runOnUiThread(() -> {
                                                XYSeries downloadSeries = new XYSeries("");
                                                downloadSeries.setTitle("");
                                                List<Double> tmpLs = new ArrayList<>(downloadRateList);
                                                int count = 0;
                                                for (Double val : tmpLs) {
                                                    downloadSeries.add(count++, val);
                                                }
                                                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                                dataset.addSeries(downloadSeries);
                                            });

                                        }
                                    }
                                    if (downloadTestFinished) {
                                        if (uploadTestFinished) {
                                            if (uploadTest.getFinalUploadRate() == 0) {
                                                System.out.println("Upload error...");
                                            } else {
                                                runOnUiThread(() -> uploadTextView.setText(dec.format(uploadTest.getFinalUploadRate()) + " Mbps"));
                                            }
                                        } else {
                                            double uploadRate = uploadTest.getInstantUploadRate();
                                            uploadRateList.add(uploadRate);
                                            position = getPositionByRate(uploadRate);
                                            runOnUiThread(() -> {
                                                rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                rotate.setInterpolator(new LinearInterpolator());
                                                rotate.setDuration(100);
                                                barImageView.startAnimation(rotate);
                                                uploadTextView.setText(dec.format(uploadTest.getInstantUploadRate()) + " Mbps");
                                            });
                                            lastPosition = position;
                                            runOnUiThread(() -> {
                                                XYSeries uploadSeries = new XYSeries("");
                                                uploadSeries.setTitle("");
                                                int count = 0;
                                                List<Double> tmpLs = new ArrayList<>(uploadRateList);
                                                for (Double val : tmpLs) {
                                                    if (count == 0) {
                                                        val = 0.0;
                                                    }
                                                    uploadSeries.add(count++, val);
                                                }
                                                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                                dataset.addSeries(uploadSeries);
                                            });
                                        }
                                    }
                                    if (pingTestFinished && downloadTestFinished && uploadTest.isFinished()) {
                                        break;
                                    }
                                    if (pingTest.isFinished()) {
                                        pingTestFinished = true;
                                    }
                                    if (downloadTest.isFinished()) {
                                        downloadTestFinished = true;
                                    }
                                    if (uploadTest.isFinished()) {
                                        uploadTestFinished = true;
                                    }
                                    if (pingTestStarted && !pingTestFinished) {
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                        }
                                    } else {
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                }
                                runOnUiThread(() -> {
                                    startButton.setEnabled(true);
                                    startButton.setTextSize(16);
                                    startButton.setText("Restart Test");
                                });
                            }
                        }).start();
                    }

                }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);
            }
        });


        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(0)
                .session(3)
                .onRatingBarFormSumbit(feedback -> {

                }).build();

        ratingDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getServer() {
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            sim.setText("" + telephonyManager.getSimCarrierIdName());
            sim.setText("" + telephonyManager.getSimOperatorName());
        }
    }
    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
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
            new AlertDialog.Builder(InternetSpeedTestActivity.this).setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel", null).show();
        }
    }
    public int getPositionByRate(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);
        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;
        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;
        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;
        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }
        return 0;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_loc1.setText(addresses.get(0).getCountryName());
            tv_loc2.setText(addresses.get(0).getAdminArea());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                getLocation();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
                if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    sim.setText("" + telephonyManager.getSimCarrierIdName());
                    sim.setText("" + telephonyManager.getSimOperatorName());
                }
        }
    }
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
    @Override
    public void onBackPressed () {
        AppManage.getInstance(InternetSpeedTestActivity.this).showInterstitialAd(InternetSpeedTestActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent=new Intent(InternetSpeedTestActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        }, "", AppManage.app_innerClickCntSwAd);
    }
    public String getIpAdddress(){
        try{
            for(Enumeration<NetworkInterface> enuNetwork=NetworkInterface.getNetworkInterfaces();
                enuNetwork.hasMoreElements();){
                NetworkInterface networkInterface=enuNetwork.nextElement();
                for(Enumeration<InetAddress> enuInet=networkInterface.getInetAddresses();
                    enuInet.hasMoreElements();){
                    InetAddress inetAddress=enuInet.nextElement();
                    if(!inetAddress.isLoopbackAddress()&& inetAddress instanceof InetAddress){
                        return  inetAddress.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
           e.getLocalizedMessage();
        }
        return "Turn on Internet...";
    }
}