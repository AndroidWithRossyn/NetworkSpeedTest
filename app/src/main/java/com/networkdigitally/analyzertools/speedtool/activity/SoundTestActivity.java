package com.networkdigitally.analyzertools.speedtool.activity;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.sound_test.FileUtil;
import com.networkdigitally.analyzertools.speedtool.sound_test.InfoDialog;
import com.networkdigitally.analyzertools.speedtool.sound_test.MyMediaRecorder;
import com.networkdigitally.analyzertools.speedtool.sound_test.Speedometer;
import com.networkdigitally.analyzertools.speedtool.sound_test.World;
import com.pesonal.adsdk.AppManage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class SoundTestActivity extends Activity {
    private static final int REQUEST_MICROPHONE = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    ArrayList<Entry> yVals;
    boolean refreshed=false;
    Speedometer speedometer;
    public static Typeface tf;
    ImageView infoButton;
    ImageView refreshButton;
    LineChart mChart;
    TextView minVal;
    TextView maxVal;
    TextView mmVal;
    TextView curVal;
    long currentTime=0;
    long savedTime=0;
    boolean isChart=false;
    boolean isMoney=false;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    /* Decibel */
    private boolean bListener = true;
    private boolean isThreadRun = true;
    private Thread thread;
    float volume = 10000;
    int refresh=0;
    private MyMediaRecorder mRecorder ;
    String[] permissions = { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE };


    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startListenAudio();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            DecimalFormat df1 = new DecimalFormat("####.0");
            if(msg.what == 1){
                if(!isChart){
                    initChart();
                    return;
                }
                speedometer.refresh();
                minVal.setText(df1.format(World.minDB));
                mmVal.setText(df1.format((World.minDB+World.maxDB)/2));
                maxVal.setText(df1.format(World.maxDB));
                curVal.setText(df1.format(World.dbCount));
                updateData(World.dbCount,0);
                if(refresh==1){
                    long now=new Date().getTime();
                    now=now-currentTime;
                    now=now/1000;
                    refresh=0;
                }else {
                    refresh++;
                }
            }
        }
    };
//        extends AppCompatActivity {
    ImageView back;
//    private TextView frequencyLabel = null;
//    private SeekBar frequencyControl = null;
//    TextView label;
//
//    private TextView delayLabel = null;
//    private SeekBar delayControl = null;
//
//    private Button playControl = null;
//
//    private SoundService mService;
//    private boolean mBound = false;
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            SoundService.LocalBinder binder = (SoundService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//
//            frequencyLabel.setText("Frequency: " + mService.getCurrentFrequency());
//            frequencyControl.setProgress(mService.getCurrentFrequency());
//
//            delayLabel.setText("Delay: " + mService.getCurrentDelay());
//            delayControl.setProgress(mService.getCurrentDelay());
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);


//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_sound_test);
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
        if (ContextCompat.checkSelfPermission(SoundTestActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SoundTestActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        }
        back = findViewById(R.id.back);
        AppManage.getInstance(SoundTestActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManage.getInstance(SoundTestActivity.this).showInterstitialAd(SoundTestActivity.this, new AppManage.MyCallback() {
                    public void callbackCall() {
                        Intent intent = new Intent(SoundTestActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }

                }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);
            }
        });
        tf= Typeface.createFromAsset(this.getAssets(), "fonts/Let_s go Digital Regular.ttf");
        minVal=(TextView)findViewById(R.id.minval);minVal.setTypeface(tf);
        mmVal=(TextView)findViewById(R.id.mmval);mmVal.setTypeface(tf);
        maxVal=(TextView)findViewById(R.id.maxval);maxVal.setTypeface(tf);
        curVal=(TextView)findViewById(R.id.curval);curVal.setTypeface(tf);
        infoButton=(ImageView) findViewById(R.id.infobutton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoDialog.Builder builder = new InfoDialog.Builder(SoundTestActivity.this);
                builder.setMessage(getString(R.string.activity_infobull));
                builder.setTitle(getString(R.string.activity_infotitle));
                builder.setNegativeButton(getString(R.string.activity_infobutton),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
        refreshButton=(ImageView)findViewById(R.id.refreshbutton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshed=true;
                World.minDB=100;
                World.dbCount=0;
                World.lastDbCount=0;
                World.maxDB=0;
                initChart();
            }
        });

        speedometer=(Speedometer)findViewById(R.id.speed);
        mRecorder = new MyMediaRecorder();
    }

    private void updateData(float val, long time) {
        if(mChart==null){
            return;
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            Entry entry=new Entry(savedTime,val);
            set1.addEntry(entry);
            if(set1.getEntryCount()>200){
                set1.removeFirst();
                set1.setDrawFilled(false);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            savedTime++;
        }
    }
    private void initChart() {
        if(mChart!=null){
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                savedTime++;
                isChart=true;
            }
        }else{
            currentTime=new Date().getTime();
            mChart = (LineChart) findViewById(R.id.chart1);
            mChart.setViewPortOffsets(50, 20, 5, 60);
            // no description text
            mChart.setDescription("");
            // enable touch gestures
            mChart.setTouchEnabled(true);
            // enable scaling and dragging
            mChart.setDragEnabled(false);
            mChart.setScaleEnabled(true);
            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);
            mChart.setDrawGridBackground(false);
            //mChart.setMaxHighlightDistance(400);
            XAxis x = mChart.getXAxis();
            x.setLabelCount(8, false);
            x.setEnabled(true);
            x.setTypeface(tf);
            x.setTextColor(Color.GREEN);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setDrawGridLines(true);
            x.setAxisLineColor(Color.GREEN);
            YAxis y = mChart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(Color.GREEN);
            y.setTypeface(tf);
            y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.GREEN);
            y.setAxisMinValue(0);
            y.setAxisMaxValue(120);
            mChart.getAxisRight().setEnabled(true);
            yVals = new ArrayList<Entry>();
            yVals.add(new Entry(0,0));
            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
            set1.setValueTypeface(tf);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.02f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setCircleColor(Color.GREEN);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.GREEN);
            set1.setFillColor(Color.GREEN);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });
            LineData data;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                data =  mChart.getLineData();
                data.clearValues();
                data.removeDataSet(0);
                data.addDataSet(set1);
            }else {
                data = new LineData(set1);
            }

            data.setValueTextSize(9f);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.getLegend().setEnabled(false);
            mChart.animateXY(2000, 2000);
            // dont forget to refresh the drawing
            mChart.invalidate();
            isChart=true;
        }

    }
    /* Sub-chant analysis */
    private void startListenAudio() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRun) {
                    try {
                        if(bListener) {
                            volume = mRecorder.getMaxAmplitude();  //Get the sound pressure value
                            if(volume > 0 && volume < 1000000) {
                                World.setDbCount(20 * (float)(Math.log10(volume)));  //Change the sound pressure value to the decibel value
                                // Update with thread
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }
                        if(refreshed){
                            Thread.sleep(1200);
                            refreshed=false;
                        }else{
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        bListener = false;
                    }
                }
            }
        });
        thread.start();
    }
    /**
     * Start recording
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            }else{
//                Toast.makeText(this, getString(R.string.activity_recStartErr), Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
//            Toast.makeText(this, getString(R.string.activity_recBusyErr), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.activity_recFileErr), Toast.LENGTH_LONG).show();
        }
        bListener = true;
    }

    /**
     * Stop recording
     */
    @Override
    protected void onPause() {
        super.onPause();
        bListener = false;
        mRecorder.delete(); //Stop recording and delete the recording file
        thread = null;
        isChart=false;
    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            isThreadRun = false;
            thread = null;
        }
        mRecorder.delete();
        super.onDestroy();
    }


    //        frequencyLabel = (TextView) findViewById(R.id.frequency_text);
//        frequencyControl = (SeekBar) findViewById(R.id.frequency_bar);
//        frequencyControl.setMax(mService.MAX_FREQUENCY_VALUE);
//
//        frequencyControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mService.updateFrequency(progress - mService.getCurrentFrequency());
//                frequencyLabel.setText("Frequency: " + mService.getCurrentFrequency() + "Hz");
//                frequencyControl.setProgress(mService.getCurrentFrequency());
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Auto-generated method stub
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                Toast.makeText(SoundActivity.this, "Frequency: " + mService.getCurrentFrequency(), Toast.LENGTH_SHORT)
////                        .show();
//            }
//        });
//
//        delayLabel = (TextView) findViewById(R.id.delay_text);
//        delayControl = (SeekBar) findViewById(R.id.delay_bar);
//        delayControl.setMax(mService.MAX_DELAY_VALUE);
//
//        delayControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mService.updateDelay(progress - mService.getCurrentDelay());
//                delayLabel.setText("Delay: " + mService.getCurrentDelay());
//                delayControl.setProgress(mService.getCurrentDelay());
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Auto-generated method stub
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                Toast.makeText(SoundActivity.this, "Delay: " + mService.getCurrentDelay(), Toast.LENGTH_SHORT)
////                        .show();
//            }
//        });
//
//        playControl = (Button) findViewById(R.id.play_button);
//        playControl.setText("Play |> ");
//        playControl.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (mService.isPaused()) {
//                    mService.resumeMusic();
//                    playControl.setText("Pause || ");
//                } else {
//                    mService.pauseMusic();
//                    playControl.setText("Play |> ");
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        //Bind to our service so we can manipulate theMediaPlayer if needed
//        Intent intent = new Intent(this, SoundService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // Unbind from the service
//        if (mBound) {
//            unbindService(mConnection);
//            mBound = false;
//        }
//    }
//
    @Override
    public void onBackPressed() {
        AppManage.getInstance(SoundTestActivity.this).showInterstitialAd(SoundTestActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(SoundTestActivity.this, DashboardActivity.class);
                startActivity(intent);
            }

        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);


    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
}
