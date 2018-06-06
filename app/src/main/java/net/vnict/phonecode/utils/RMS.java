package net.vnict.phonecode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class RMS {
    private static RMS sInstance;
    private final String LAUNCH_APP = "launch_app";
    private Context mContext;
    private SharedPreferences mSharePreference;
    private int mLaunch_app = 0;

    public static RMS getInstance() {
        if (sInstance == null)
            sInstance = new RMS();
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void increaseNumberOfLaunchApp() {
        mLaunch_app++;
        Utils.LOG("Save mLaunch_app = " + mLaunch_app);
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putInt(LAUNCH_APP, mLaunch_app);
        editor.commit();
    }

    public int getNumberOfLaunchApp() {
        return mLaunch_app;
    }

    public boolean isFirstLaunchApp() {
        return mLaunch_app == 0;
    }


    public void load() {
        mLaunch_app = mSharePreference.getInt(LAUNCH_APP, mLaunch_app);
        Utils.LOG("----------------Load RMS---------------");
        Utils.LOG("mLaunch_app = " + mLaunch_app);
    }
}
