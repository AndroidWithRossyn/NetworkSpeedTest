package com.networkdigitally.analyzertools.speedtool.activity;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.networkdigitally.analyzertools.speedtool.R;
import com.pesonal.adsdk.AppManage;

public class PrivacyPolicyActivity extends AppCompatActivity {
    RelativeLayout agree_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        agree_btn = findViewById(R.id.agree_btn);
        AppManage.getInstance(PrivacyPolicyActivity.this).showNative((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivacyPolicyActivity.this, GetStartedActivity.class);
                startActivity(intent);
            }
        });
    }
}