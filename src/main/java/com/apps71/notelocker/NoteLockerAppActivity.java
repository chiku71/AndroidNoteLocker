package com.apps71.notelocker;

/***
 * Class to be Parent of all activities in the Application.
 *
 *
 *
 */


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

class NoteLockerAppActivity extends AppCompatActivity
{
    protected static boolean lockCurrentScreen = false;

    private InactivityWatcherThread watcherThread = null;
    private boolean movedToAnotherActivity = false;

    private BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Adding secure flags to Prevent Screenshot and Prevent screen from OverView/Recent Activities View ...
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

    }

    @Override
    protected void onPause()
    {
        if (!movedToAnotherActivity && this.broadcastReceiver == null)
        {
            this.broadcastReceiver = this.registerListenersForClickOnRecentAppsBtn();
        }

        super.onPause();

        if (this.isAppRunningInBackground() && this.watcherThread == null)
        {
            this.watcherThread = new InactivityWatcherThread(this.getToolTimeOutMinsInMilliSecs());
            this.watcherThread.start();
        }
    }

    @Override
    protected void onResume()
    {
        this.stopOngoingInactivityWatcher();
        this.unregisterBroadcastReceiver();
        super.onResume();

        if (lockCurrentScreen)
        {
            lockCurrentScreen = false;

            Intent nextPageActivity = new Intent(getApplicationContext(), LockApplication.class);
            nextPageActivity.putExtra(Constants.FROM_ACTIVITY, "PageInactivity");
            startActivity(nextPageActivity);
        }
    }

    @Override
    public void startActivity(Intent intent)
    {
        this.movedToAnotherActivity = true;
        super.startActivity(intent);
    }

    private int getToolTimeOutMinsInMilliSecs()
    {
        ToolSettingConfig settingConfig = new ToolSettingConfig(getApplicationContext());
        int inactiveTimeOutMins = settingConfig.getToolTimeOutMins();
        return inactiveTimeOutMins*60*1000;
    }

    private BroadcastReceiver registerListenersForClickOnRecentAppsBtn()
    {
        IntentFilter intentFilterACSD = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null)
                    {
                        if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS))
                        {
                            // RecentApp or Overview Button has been click
                            if (watcherThread == null && !lockCurrentScreen)
                            {
                                // Adding New Inactivity Watcher Thread
                                watcherThread = new InactivityWatcherThread(getToolTimeOutMinsInMilliSecs());
                                watcherThread.start();
                            }
                        }
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilterACSD);

        return broadcastReceiver;
    }

    private void unregisterBroadcastReceiver()
    {
        if (this.broadcastReceiver != null)
        {
            unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
            // Broadcast receiver Unregistered ...
        }
    }

    private void stopOngoingInactivityWatcher()
    {
        if (this.watcherThread != null)
        {
            try
            {
                this.watcherThread.terminate();
            }
            catch (Exception ex)
            {
                // Error while stopping the thread
            }
            this.watcherThread = null;

            // Running Watcher Thread has been stopped ...
        }
    }

    public boolean isAppRunningInBackground()
    {
        Context appContext = getApplicationContext();
        try
        {
            ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            // The first in the list of RunningTasks is always the foreground task.
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

            // Get the top fore ground activity
            String foreGroundTaskPkgName = foregroundTaskInfo.topActivity.getPackageName();
            PackageManager pkgMgr = appContext.getPackageManager();
            PackageInfo foreGroundAppPkgInfo = pkgMgr.getPackageInfo(foreGroundTaskPkgName, 0);

            String foreGroundAppName = foreGroundAppPkgInfo.applicationInfo.loadLabel(pkgMgr).toString();

            String foreGroundAppFullName = foreGroundTaskPkgName + "." + foreGroundAppName;
            String thisAppFullName = getPackageName() + "." + this.getApplicationName();

            if (!foreGroundAppFullName.equals(thisAppFullName))
            {
                // App is running in background ...
                return true;
            }
        }
        catch (Exception ex)
        {
            // Exception occurred while checking for APP Running Status
        }
        return false;
    }


    protected String getApplicationName()
    {
        return getString(R.string.app_name);
    }

    @Override
    protected void onDestroy()
    {
        this.unregisterBroadcastReceiver();
        super.onDestroy();
    }
}
