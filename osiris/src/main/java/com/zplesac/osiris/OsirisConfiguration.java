package com.zplesac.osiris;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

public class OsirisConfiguration {

    private static final int DEFAULT_COUNT = 6;
    private static final long DEFAULT_INSTALL_TIME = TimeUnit.DAYS.toMillis(5);
    private static final String PREFS_NAME = "osiris_prefs";

    private int triggerCount = DEFAULT_COUNT;
    private long minInstallTime = DEFAULT_INSTALL_TIME;
    private Context context;
    private SharedPreferences sharedPrefs;

    private OsirisConfiguration(Builder builder) {
        triggerCount = builder.triggerCount;
        minInstallTime = builder.minInstallTime;
        context = builder.context;
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public long getMinInstallTime() {
        return minInstallTime;
    }

    public Context getContext() {
        return context;
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public static final class Builder {

        private int triggerCount;
        private long minInstallTime;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder triggerCount(int val) {
            triggerCount = val;
            return this;
        }

        public Builder minInstallTime(long val) {
            minInstallTime = val;
            return this;
        }

        public OsirisConfiguration build() {
            return new OsirisConfiguration(this);
        }
    }
}
