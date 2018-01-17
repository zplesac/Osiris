package com.zplesac.osiris;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class Osiris {

    private static final String KEY_LAUNCH_COUNT = "osiris_launch_count";
    private static final String KEY_BOOL_ASKED = "osiris_asked";
    private static final String KEY_LONG_FIRST_LAUNCH = "osiris_first_launch";
    private static final int DEFAULT_REPEAT_COUNT = 30;

    private static volatile Osiris instance;

    private OsirisConfiguration configuration;

    private Osiris() {
        // Private constructor.
    }

    /**
     * Get current library instance.
     *
     * @return Current library instance.
     */
    public static Osiris getInstance() {
        if (instance == null) {
            synchronized (Osiris.class) {
                if (instance == null) {
                    instance = new Osiris();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize this instance with provided configuration.
     *
     * @param configuration ConnectionBuddy configuration which is used in instance.
     */
    public synchronized void init(OsirisConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        if (this.configuration == null) {
            this.configuration = configuration;
        }
    }

    public OsirisConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Checks if the app has been launched often enough to ask for a rating, and shows the rating
     * request if so. The rating request can be a SnackBar (preferred) or a dialog.
     */
    public boolean shouldShowRequest() {
        final boolean asked = configuration.getSharedPrefs().getBoolean(KEY_BOOL_ASKED, false);
        final long firstLaunch = configuration.getSharedPrefs().getLong(KEY_LONG_FIRST_LAUNCH, 0);
        final boolean shouldShowRequest =
                getRemainingCount() == 0
                        && !asked
                        && System.currentTimeMillis() > firstLaunch + configuration.getMinInstallTime();
        return shouldShowRequest && canRateApp();
    }

    /**
     * Call this method whenever your app is launched to increase the launch counter. Or whenever
     * the user performs an action that indicates immersion.
     */
    public void track() {
        SharedPreferences.Editor editor = configuration.getSharedPrefs().edit();

        // Get current launch count
        long count = getCount();
        count++;
        editor.putLong(KEY_LAUNCH_COUNT, count).apply();

        // Save first launch timestamp
        if (configuration.getSharedPrefs().getLong(KEY_LONG_FIRST_LAUNCH, -1) == -1) {
            editor.putLong(KEY_LONG_FIRST_LAUNCH, System.currentTimeMillis());
        }
        editor.apply();
    }

    /**
     * Returns how many more times the trigger action should be performed before it triggers the
     * rating request. This can be either the first request or consequent requests after dismissing
     * previous ones. This method does NOT consider if the request will be shown at all, e.g. when
     * "don't ask again" was checked.
     * <p>
     * If this method returns `0` (zero), the next call to {@link #shouldShowRequest()} ()} will show the dialog.
     * </p>
     *
     * @return Remaining count before the next request is triggered.
     */
    public long getRemainingCount() {
        long count = getCount();
        if (count < configuration.getTriggerCount()) {
            return configuration.getTriggerCount() - count;
        } else {
            return (DEFAULT_REPEAT_COUNT - ((count - configuration.getTriggerCount()) % DEFAULT_REPEAT_COUNT)) % DEFAULT_REPEAT_COUNT;
        }
    }

    /**
     * Returns how often The Action has been performed, ever. This is usually the app launch event.
     *
     * @return Number of times the app was launched.
     */
    private long getCount() {
        return configuration.getSharedPrefs().getLong(KEY_LAUNCH_COUNT, 0L);
    }

    /**
     * Creates an Intent to launch the proper store page. This does not guarantee the Intent can be
     * launched (i.e. that the Play Store is installed).
     *
     * @return The Intent to launch the store.
     */
    public Intent getStoreIntent() {
        final Uri uri = Uri.parse("market://details?id=" + configuration.getContext().getPackageName());
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    /**
     * Resets all data saved by Osiris. This is not advised in production builds
     * as behavior against user preferences can occur.
     */
    public void reset() {
        configuration.getSharedPrefs().edit().clear().apply();
    }

    /**
     * Checks if the app can be rated, i.e. if the store Intent can be launched, i.e. if the Play
     * Store is installed.
     *
     * @return if the app can be rated
     * @see #getStoreIntent()
     */
    private boolean canRateApp() {
        return canOpenIntent(getStoreIntent());
    }

    /**
     * Checks if the system or any 3rd party app can handle the Intent
     *
     * @param intent the Intent
     * @return if the Intent can be handled by the system
     */
    private boolean canOpenIntent(Intent intent) {
        return configuration.getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }
}
