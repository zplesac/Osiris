package com.zplesac.sampleapp;

import android.app.Application;

import com.zplesac.osiris.Osiris;
import com.zplesac.osiris.OsirisConfiguration;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OsirisConfiguration configuration = new OsirisConfiguration.Builder(this).minInstallTime(0).triggerCount(3).build();
        Osiris.getInstance().init(configuration);
    }
}
