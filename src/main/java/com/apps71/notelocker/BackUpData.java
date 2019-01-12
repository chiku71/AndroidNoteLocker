package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class BackUpData extends NoteLockerAppActivity implements View.OnClickListener
{
    /*Back Up NoteLocker Data:
     * 1) Backup To Local Drive.
     *      -> Create a new File in User's internal Storage.
     *      -> Read the encrypted data from the UserInfo and AppData files.
     *      -> Prepare those info in the below JSON Format and encrypt it with the Backup Enc key.
     *          {
     *              "USER_INFO": <UserInfo Encrypted Text>,
     *              "APP_INFO": <AppInfo Encrypted Text>
     *           }
     *      -> Write the above encrypted data to the backup file.
     * 2) Backup to User's Google Drive.
     *      -> TODO : Pending for implementation
     *
     */
    // Class variables
    private ImageButton backUpToLocal, backUpToGoogleDrive;
    private TextView outputMessage;
    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up_data);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.initializeUIElements();
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

    private void initializeUIElements()
    {
        this.backUpToLocal = findViewById(R.id.back_up_to_local_drive);
        this.backUpToGoogleDrive = findViewById(R.id.back_up_to_google_drive);
        this.outputMessage = findViewById(R.id.output_message_back_up_data);
    }

    private void setUpActionListeners()
    {
        this.backUpToLocal.setOnClickListener(this);
        this.backUpToGoogleDrive.setOnClickListener(this);
    }

    @Override
    public void onBackPressed()
    {
        this.goToNextPage(UserHome.class);
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(BackUpData.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_up_to_local_drive:
                new BackUpDataToLocalDrive(getApplicationContext(), this.outputMessage).runBackUp();
                break;

            case R.id.back_up_to_google_drive:
                //TODO: Implement for backup to google drive
                String msg ="The developer is currently working on this.\nPlease wait for the upcoming updates to use this feature.";
                ToastUtils.showInfoForLongTime(this, msg);
                break;
        }
    }

    // Method to use along with the Code to Get Directory
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case 9999:
                break;
        }
    }

}
