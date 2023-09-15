package com.networkdigitally.analyzertools.speedtool;

import android.location.Location;

public interface GPSCallBack {
    public abstract void onGPSUpdate(Location location);
}
