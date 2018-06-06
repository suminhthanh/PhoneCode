package net.vnict.phonecode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class RMS {
    private static RMS sInstance;
    private final String DOWNLOAD_DATA = "download_data";
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

    public void increaseNumberOfDownloadData() {
        mLaunch_app++;
        Utils.LOG("Save download_data = " + mLaunch_app);
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putInt(DOWNLOAD_DATA, mLaunch_app);
        editor.commit();
    }

    public int getNumberOfDownloadData() {
        return mLaunch_app;
    }

    public boolean isFirstLaunchApp() {
        return mLaunch_app == 0;
    }
    public void load() {
        mLaunch_app = mSharePreference.getInt(DOWNLOAD_DATA, mLaunch_app);
        Utils.LOG("----------------Load RMS---------------");
        Utils.LOG("download_data = " + mLaunch_app);
    }
}
