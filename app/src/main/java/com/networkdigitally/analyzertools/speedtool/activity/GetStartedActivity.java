package com.networkdigitally.analyzertools.speedtool.activity;
import static com.pesonal.adsdk.AppManage.ADMOB_N;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.networkdigitally.analyzertools.speedtool.R;
import com.pesonal.adsdk.AppManage;
public class GetStartedActivity extends AppCompatActivity {
    CheckBox checkbox;
    TextView img3;
    TextView privacy_policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        img3=findViewById(R.id.img3);
        checkbox=findViewById(R.id.checkBox);
        privacy_policy=findViewById(R.id.privacy_policy);
        AppManage.getInstance(GetStartedActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        privacy_policy.setOnClickListener(v -> gotoUrl("https://chatterbuzzmediainc.blogspot.com/2022/08/privacy-policy.html"));
        AppManage.getInstance(GetStartedActivity.this).loadInterstitialAd(this);

        img3.setOnClickListener(v -> {
            if(checkbox.isChecked()==true){
                AppManage.getInstance(GetStartedActivity.this).showInterstitialAd(GetStartedActivity.this, new AppManage.MyCallback() {
                    public void callbackCall() {
                        Intent intent = new Intent(GetStartedActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                },"",AppManage.app_mainClickCntSwAd);
            } else {
                Toast.makeText(GetStartedActivity.this, "Please Accept Privacy Policy", Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}