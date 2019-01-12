package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ToolSettings extends NoteLockerAppActivity implements View.OnClickListener
{
    /***
     * This activity enables user to change setting in this application such as :-
     *      1) Change password
     *      2) Change Security Question/Answer
     *      3) Wipe all Notes
     *      4) Set Tool Inactive Time Out Period
     */

    private Button changePasswordBtn, changeSeqQnABtn, deleteAllNotesBtn;
    private TextView toolTimeOutMinsFld;

    private ToolSettingConfig settingConfig;
    private int initialToolTimeOutMins;
    private int currentTimeOutOnDisplay;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.initializeUIVariables();
            this.loadToolConfigsToUI();
            this.setUpActionListeners();

            //Adding the back button to the App Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class);
        }
    }

    private void setUpActionListeners()
    {
        this.changePasswordBtn.setOnClickListener(this);
        this.changeSeqQnABtn.setOnClickListener(this);
        this.deleteAllNotesBtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.changePasswordBtn = findViewById(R.id.change_current_password_settings);
        this.changeSeqQnABtn = findViewById(R.id.change_current_security_qa_settings);
        this.deleteAllNotesBtn = findViewById(R.id.delete_all_notes_settings);

        this.toolTimeOutMinsFld = findViewById(R.id.tool_time_out_mins_settings);

    }

    private void loadToolConfigsToUI()
    {
        this.settingConfig = new ToolSettingConfig(getApplicationContext());
        this.initialToolTimeOutMins = this.settingConfig.getToolTimeOutMins();
        this.currentTimeOutOnDisplay = this.initialToolTimeOutMins;

        this.displayTimeOutMins();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.change_current_password_settings:
                // Select the backup file using File picker
                this.goToNextPage(ChangePassword.class);
                break;

            case R.id.change_current_security_qa_settings:
                // Process the user backup file for first use.
                this.goToNextPage(ChangeSecurityQA.class);
                break;

            case R.id.delete_all_notes_settings:
                // Process the user backup file for first use.
                //Toast.makeText(this, "Clicked to delete all Notes.", Toast.LENGTH_LONG).show();
                this.goToNextPage(DeleteAllNotes.class);
                break;
        }
    }

    public void decreaseTimeOut(View view)
    {
        if (this.currentTimeOutOnDisplay == 1)
        {
            ToastUtils.showInfoForShortTime(getApplicationContext(), "Tool Time Out period can't be less than 1 Minute.");
        }
        else
        {
            this.currentTimeOutOnDisplay--;
            this.displayTimeOutMins();
        }
    }

    public void increaseTimeOut(View view)
    {
        if (this.currentTimeOutOnDisplay == 30)
        {
            ToastUtils.showInfoForShortTime(getApplicationContext(), "Please don't set Tool Time Out period to more than 30 Minutes.");
        }
        else
        {
            this.currentTimeOutOnDisplay++;
            this.displayTimeOutMins();
        }
    }

    private void displayTimeOutMins()
    {
        String timeOutMinsFormated = "" + this.currentTimeOutOnDisplay;
        if (this.currentTimeOutOnDisplay < 10)
        {
            timeOutMinsFormated = "0" + this.currentTimeOutOnDisplay;
        }
        this.toolTimeOutMinsFld.setText(timeOutMinsFormated + " Mins");
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(ToolSettings.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    private void saveSettingsConfig()
    {
        if (this.initialToolTimeOutMins != this.currentTimeOutOnDisplay)
        {
            this.settingConfig.setToolTimeOutMins(this.currentTimeOutOnDisplay);
            this.settingConfig.storeData();
            this.initialToolTimeOutMins = this.currentTimeOutOnDisplay;
            ToastUtils.showSuccessMessage(getApplicationContext(), "Setting configurations have been saved successfully.");
        }
        else
        {
            // No Settings Config has been changed ...
        }
    }


    @Override
    public void onBackPressed()
    {
        this.goToNextPage(UserHome.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.save_settings_config:
                this.saveSettingsConfig();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_tool_settings, menu);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ToolUtils.closeApplication();
    }
}
