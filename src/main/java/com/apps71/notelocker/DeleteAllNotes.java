package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class DeleteAllNotes extends NoteLockerAppActivity implements View.OnClickListener
{
    ImageButton yesProceedBtn, noAbortBtn;
    EditText enteredPasswordFld;

    UserDataJson userData;
    String className = ToolUtils.getClassNameFromObject(this);
    String enteredPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_all_notes);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.initializeUIVariables();
            this.setUpActionListeners();

            //Adding the back button to the App Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Getting User Data
            this.userData = new UserDataJson(getApplicationContext());
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class);
        }
    }

    private void setUpActionListeners()
    {
        this.yesProceedBtn.setOnClickListener(this);
        this.noAbortBtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.enteredPasswordFld = findViewById(R.id.password_dan);
        this.yesProceedBtn = findViewById(R.id.yes_delete_all_notes_dan);
        this.noAbortBtn = findViewById(R.id.no_stop_here_dan);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.yes_delete_all_notes_dan:
                this.processInputData();
                break;

            case R.id.no_stop_here_dan:
                this.onBackPressed();
                break;
        }
    }


    private void processInputData()
    {
        this.enteredPassword = this.enteredPasswordFld.getText().toString().trim();

        if (this.enteredPassword.length() == 0)
        {
            ToastUtils.showFailureMessage(this, "Please enter current password.");
        }
        else if (this.enteredPassword.length() < 8)
        {
            ToastUtils.showFailureMessage(this, "\"Please enter a valid password.\nHint: Password contains at least eight characters.");
        }
        else
        {
            String userPassword = this.userData.getPassword();
            if (userPassword.equals(this.enteredPassword))
            {
                try
                {
                    this.deleteAllNotes();
                    ToastUtils.showSuccessMessage(this, "All your notes have been deleted successfully.");
                    this.onBackPressed();
                }
                catch (Exception ex)
                {
                    String msg = "Some issue occurred while updating Security Q&A.";
                    ToastUtils.showFailureMessage(this, msg + "\nPlease try again later.");
                }
            }
            else
            {
                ToastUtils.showFailureMessage(this, "Entered password didn't match. Please try again ...");
            }
        }
    }


    private void deleteAllNotes()
    {
        AllNotes allNotes = new AllNotes(getApplicationContext());
        allNotes.removeAllNotes();
        allNotes.storeAllNotesInFile();
    }


    @Override
    public void onBackPressed()
    {
        this.goToNextPage(ToolSettings.class);
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(DeleteAllNotes.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ToolUtils.closeApplication();
    }
}
