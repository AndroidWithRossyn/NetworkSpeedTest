package com.networkdigitally.analyzertools.speedtool.activity;

import static com.pesonal.adsdk.AppManage.ADMOB_N;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.ChannelInterference;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.ConnectionInfo;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.HomeTesting;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.NetworkStatus;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.SettingsExtraActivity;
import com.networkdigitally.analyzertools.speedtool.wifiAnalyzer.StrengthGraph;
import com.pesonal.adsdk.AppManage;

import java.util.Locale;

public class IPAddressTestActivity extends AppCompatActivity implements ActionBar.TabListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_test);
        back = findViewById(R.id.back);
        AppManage.getInstance(IPAddressTestActivity.this).showNativeBanner((ViewGroup) findViewById(R.id.native_ads), ADMOB_N[0], "");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManage.getInstance(IPAddressTestActivity.this).showInterstitialAd(IPAddressTestActivity.this, new AppManage.MyCallback() {
                    public void callbackCall() {
                        Intent intent = new Intent(IPAddressTestActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }

                }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);
            }
        });
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });
        Drawable drawable[] = new Drawable[mSectionsPagerAdapter.getCount()];
        drawable[0] = getResources().getDrawable(R.drawable.icon_tab_cinf);
        drawable[1] = getResources().getDrawable(R.drawable.icon_tab_ns);
        drawable[2] = getResources().getDrawable(R.drawable.icon_tab_ht);
        drawable[3] = getResources().getDrawable(R.drawable.icon_tab_ci);
        drawable[4] = getResources().getDrawable(R.drawable.icon_tab_sg);
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            if (actionBar != null) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setTabListener(this).setIcon(drawable[i]));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsExtraActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        }
        assert actionBar != null;
        switch (tab.getPosition()) {
            case 0:
                actionBar.setSubtitle(getString(R.string.title_section_name1));
                break;
            case 1:
                actionBar.setSubtitle(getString(R.string.title_section_name2));
                break;
            case 2:
                actionBar.setSubtitle(getString(R.string.title_section_name3));
                break;
            case 3:
                actionBar.setSubtitle(getString(R.string.title_section_name4));
                break;
            case 4:
                actionBar.setSubtitle(getString(R.string.title_section_name5));
                break;
        }
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * -----------------------------------------------------------------------------------
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * --------------------------------------------------------------------------------------
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ConnectionInfo();
                case 1:
                    return new NetworkStatus();
                case 2:
                    return new HomeTesting();
                case 3:
                    return new ChannelInterference();
                case 4:
                    return new StrengthGraph();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * -------------------------------------------------------------------------------------------
     * A placeholder fragment containing a simple view.
     * ----------------------------------------------------------------------------------------------
     */
    @SuppressWarnings("unused")
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        @SuppressWarnings("unused")
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    @Override
    public void onBackPressed() {
        AppManage.getInstance(IPAddressTestActivity.this).showInterstitialAd(IPAddressTestActivity.this, new AppManage.MyCallback() {
            public void callbackCall() {
                Intent intent = new Intent(IPAddressTestActivity.this, DashboardActivity.class);
                startActivity(intent);
            }

        }, AppManage.ADMOB, AppManage.app_mainClickCntSwAd);


    }
}