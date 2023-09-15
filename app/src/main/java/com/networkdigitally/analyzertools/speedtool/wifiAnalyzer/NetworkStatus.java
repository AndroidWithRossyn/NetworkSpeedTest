package com.networkdigitally.analyzertools.speedtool.wifiAnalyzer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.networkdigitally.analyzertools.speedtool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkStatus extends Fragment
{
    @SuppressWarnings("unused")
    public static final String LOG_TAG = NetworkStatus.class.getSimpleName();

    private WifiScanReceiver mWifiReceiver;
    private WifiManager mWifiManager;

    private int mRefreshRateInSec = 2;
    private int mRrefreshRate = 1000 * mRefreshRateInSec;
    private Timer timer;
    private ViewHolder viewHolder;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mWifiManager =(WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiScanReceiver();

        Utility.enableWifi(mWifiManager);

        mWifiManager.startScan();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        setParametersReceiverBefore();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        setParametersReceiverAfter();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.network_status_tab, container, false);

        viewHolder = new ViewHolder(rootView);

        return rootView;
    }

    //unregister receiver & close timer
    private void setParametersReceiverBefore()
    {
        getActivity().unregisterReceiver(mWifiReceiver);

        if (timer != null)
        {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //register receiver & schedule timer
    private void setParametersReceiverAfter()
    {
        getActivity().registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //schedule timer
        if(mRrefreshRate > 0)
        {
            timer = new Timer();
            TimerTask updateTask = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            updateView();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(updateTask, 0, mRrefreshRate);
        }
    }

    //update View - Networks & Bar
    private void updateView()
    {
        Utility.enableWifi(mWifiManager);
        mWifiManager.startScan();

        List<ScanResult> wifiScanList = mWifiManager.getScanResults();

        if(wifiScanList == null)
        {
            return;
        }

        updateNetworkStatus(wifiScanList);
        updateInfoBar(wifiScanList.size());
    }
    //TODO create progress dialog during update
    //update network status for each signal
    private void updateNetworkStatus(List<ScanResult> wifiScanList)
    {
        List<String[]> list = new ArrayList<>();

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        String bssid = wifiInfo.getBSSID();
        if( bssid == null)
            bssid = "";

        for (int i = 0; i < wifiScanList.size(); i++)
        {
            String [] wifiDetails = new String[NetworkStatusAdapter.SIZE_TAB];

            wifiDetails[NetworkStatusAdapter.BSSID_TAB] = wifiScanList.get(i).BSSID;
            wifiDetails[NetworkStatusAdapter.SSID_TAB] = wifiScanList.get(i).SSID;
            wifiDetails[NetworkStatusAdapter.CAPABILITIES_TAB] = wifiScanList.get(i).capabilities;
            wifiDetails[NetworkStatusAdapter.FREQUENCY_TAB] = String.valueOf(wifiScanList.get(i).frequency);
            wifiDetails[NetworkStatusAdapter.LEVEL_TAB] = String.valueOf(wifiScanList.get(i).level);

            if(bssid.equals(wifiScanList.get(i).BSSID))
            {
                wifiDetails[NetworkStatusAdapter.IS_CONNECTED] = "1";
            }
            else
            {
                wifiDetails[NetworkStatusAdapter.IS_CONNECTED] = "0";
            }

            list.add(wifiDetails);
        }

        NetworkStatusAdapter mNetworkStatusAdapter = new NetworkStatusAdapter(getActivity(), list);
        viewHolder.mListView.setAdapter(mNetworkStatusAdapter);
    }

    //update view in simple info bar
    @SuppressWarnings("deprecation")
    private void updateInfoBar(int size)
    {
        Resources resources = getResources();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        viewHolder.intervalView.setText(String.format(resources.getString(R.string.ns_interval), mRefreshRateInSec));
        viewHolder.wirelessNetworksView.setText(String.format(resources.getString(R.string.ns_number_of_available_network), size));

        if(wifiInfo.getBSSID() == null)
            return;

        viewHolder.connectedInfoView.setText(String.format(resources.getString(R.string.connected_bar), wifiInfo.getSSID()));
        viewHolder.ipView.setText(String.format(resources.getString(R.string.ns_ip), Formatter.formatIpAddress(wifiInfo.getIpAddress())));
        viewHolder.speedView.setText(String.format(resources.getString(R.string.ns_speed), wifiInfo.getLinkSpeed()));
    }

    //create dialog to choice refresh interval
    private void createRefreshIntervalDialog()
    {
        final String[] stringsRefreshRateValues = getResources().getStringArray(R.array.pref_refresh_rate_values);
        final String[] stringsRefreshRateOptions = getResources().getStringArray(R.array.pref_refresh_rate_options);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.ns_title_dialog_refresh_rate);

        alertDialog.setSingleChoiceItems(stringsRefreshRateOptions, 1,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mRefreshRateInSec = Integer.parseInt(stringsRefreshRateValues[which]);
                        mRrefreshRate = 1000 * mRefreshRateInSec;

                        NetworkStatus.this.setParametersReceiverBefore();
                        NetworkStatus.this.setParametersReceiverAfter();
                        NetworkStatus.this.updateView();

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    //update if AS FAST AS POSSIBLE
    private class WifiScanReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            if( mRrefreshRate == 0)
            {
                updateView();
            }
        }
    }

    public class ViewHolder
    {
        public final ImageButton refreshButton;
        public final ImageButton refreshRateButton;

        public final TextView connectedInfoView;
        public final TextView intervalView;
        public final TextView ipView;
        public final TextView speedView;
        public final TextView wirelessNetworksView;

        public final ListView mListView;

        public ViewHolder(View rootView)
        {
            refreshButton = (ImageButton) rootView.findViewById(R.id.refresh_button);
            refreshRateButton = (ImageButton) rootView.findViewById(R.id.scanning_time_button);

            connectedInfoView = (TextView) rootView.findViewById(R.id.ns_connected_textview);
            intervalView = (TextView) rootView.findViewById(R.id.ns_interval_textview);
            ipView = (TextView) rootView.findViewById(R.id.ns_ip_textView);
            speedView = (TextView) rootView.findViewById(R.id.ns_speed_textView);
            wirelessNetworksView = (TextView) rootView.findViewById(R.id.ns_number_of_available_network_textView);

            mListView = (ListView) rootView.findViewById(R.id.network_status_listview);

            refreshButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    updateView();
                }
            });

            refreshRateButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    createRefreshIntervalDialog();
                }
            });
        }
    }
}
