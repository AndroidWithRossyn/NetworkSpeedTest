package com.networkdigitally.analyzertools.speedtool.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.networkdigitally.analyzertools.speedtool.R;
public class ThankyouActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}