package com.apps71.notelocker;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

class PackageUtils
{
    protected static String getVersionName(Context appContext)
    {
        try
        {
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return "1.0";
        }
    }

    protected static int getVersionCode(Context appContext)
    {
        try
        {
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return 1;
        }
    }
}
