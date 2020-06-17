package com.apps71.notelocker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

class FileAccessHelper
{
    private Context appContext;

    public FileAccessHelper(Context appContext)
    {
        this.appContext = appContext;
    }

    public boolean isStorageAccessGranted()
    {
        int externalStorageWritePermission = ActivityCompat.checkSelfPermission(this.appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (externalStorageWritePermission == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }

    public void showNoStoragePermissionError()
    {
        String noPermissionMsg = "Please grant Storage permission to this app.";
        ToastUtils.showFailureMessage(this.appContext, noPermissionMsg);
        this.openApplicationPermissionPage();
    }

    public void openApplicationPermissionPage()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.appContext.getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.appContext.startActivity(intent);
    }
}
