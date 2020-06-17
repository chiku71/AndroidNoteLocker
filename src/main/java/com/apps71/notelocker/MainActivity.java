package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity
{
    String className = ToolUtils.getClassNameFromObject(this);

    //Main method from where everything starts
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if( getIntent().getBooleanExtra(Constants.EXIT_APPLICATION, false))
        {
            this.finish();
        }
        else
        {
            try
            {
                Context appContext = getApplicationContext();
                if (ToolUtils.checkUserDataFileExistence(appContext) &&
                        ToolUtils.checkAppDataFileExistence(appContext) &&
                        ToolUtils.checkSettingsConfigFileExistence(appContext))
                {
                    this.goToNextPage(LoginActivity.class);
                }
                else
                {
                    this.goToNextPage(NewInstallationRunner.class);
                }
            }
            catch (Exception ex)
            {
                // Exception Occurred
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(MainActivity.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        SessionManager.endAppSession(getApplicationContext());
        ToolUtils.closeApplication();
    }
}
