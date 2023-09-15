package com.networkdigitally.analyzertools.speedtool.activity;
import static com.pesonal.adsdk.AppManage.ADMOB_N;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.networkdigitally.analyzertools.speedtool.R;
import com.pesonal.adsdk.AppManage;
public class ExitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        TextView cancel_btn = findViewById(R.id.cancel_btn);
        TextView rate_btn = findViewById(R.id.rate_btn);
        TextView yes_btn = findViewById(R.id.yes_btn);
        AppManage.getInstance(ExitActivity.this).showNative((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        cancel_btn.setOnClickListener(v -> ExitActivity.super.onBackPressed());
        rate_btn.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });
        yes_btn.setOnClickListener(v -> {
            startActivity(new Intent(ExitActivity.this, ThankyouActivity.class));
            finish();
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExitActivity.super.onBackPressed();
            }
        });
    }
}