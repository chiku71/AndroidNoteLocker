package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangePassword extends NoteLockerAppActivity implements View.OnClickListener
{
    /**
     * This activity enables user to Change the current password.
     * The process follows as below :
     *      1) Authenticate the current password.
     *      2) Accept the new password.
     *      3) Decrypt the AppData file with the current password and
     *          encrypt it again with the new password.
     *      4) Update the new password to the UserData file.
     */

    private EditText oldPasswordFld, newPasswordFld, reenteredNewPasswordFld;
    private ImageButton changePasswordBtn;
    private ShapeDrawable shapePw, shapeRePw;
    private UserDataJson userData;

    private String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.initializeUIVariables();
            this.setUpActionListeners();
            this.initiateShapesForPasswordFields();
            this.getUserDataJson();

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
    }

    private void initializeUIVariables()
    {
        this.oldPasswordFld = findViewById(R.id.oldPasswordCP);
        this.newPasswordFld = findViewById(R.id.newPasswordCP);
        this.reenteredNewPasswordFld = findViewById(R.id.newPasswordConfirmCP);
        this.changePasswordBtn = findViewById(R.id.changePasswordCP);
    }

    private void initiateShapesForPasswordFields()
    {
        this.shapePw = UiEditNoteUtils.getPasswordFieldShape();
        this.shapeRePw = UiEditNoteUtils.getPasswordFieldShape();
    }

    private void getUserDataJson()
    {
        Context appContext = getApplicationContext();
        this.userData = new UserDataJson(appContext);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.changePasswordCP:
                this.processInputData();
                break;
        }
    }

    private void processInputData()
    {
        // Method to validate and process the input data
        String oldStoredPassword = this.userData.getPassword();
        String enteredOldPassword = this.oldPasswordFld.getText().toString().trim();
        String newPassword = this.newPasswordFld.getText().toString().trim();
        String reEnteredPassword = this.reenteredNewPasswordFld.getText().toString().trim();

        if (enteredOldPassword.equals(oldStoredPassword))
        {
            if (newPassword.length() >= 8)
            {
                if (newPassword.equals(reEnteredPassword))
                {
                    this.processNewPasswordForUpdate(newPassword, oldStoredPassword);
                }
                else
                {
                    ToastUtils.showFailureMessage(this, "New Password from both fields didn't match.\nPlease try again ...");
                }
            }
            else
            {
                ToastUtils.showFailureMessage(this, "Password should be of at least 8 characters.\nPlease try again ...");
            }
        }
        else
        {
            ToastUtils.showFailureMessage(this, "Entered current password is incorrect.\nPlease try again ...");
        }
    }

    private void processNewPasswordForUpdate(String newPassword, String oldPassword)
    {
        // Method to process the New Password
        boolean passwordUpdateSuccessful = this.updatePassword(newPassword, true);

        if (passwordUpdateSuccessful)
        {
            boolean encSuccessful = this.encryptAppDataWithNewKey(newPassword);

            if (encSuccessful)
            {
                ToastUtils.showSuccessMessage(this, "Your password has been changed successfully.\nPlease login again ...");
                this.goToNextPage(LoginActivity.class);
            }
            else
            {
                this.updatePassword(oldPassword, false);
                ToastUtils.showFailureMessage(this, "Unable to update your password due to some technical issue.\nPlease try again later ...");
            }
        }

    }

    private boolean updatePassword(String newPassword, boolean showErrorMsg)
    {
        boolean successStatus = false;
        try
        {
            this.userData.setPassword(newPassword);
            Context appContext = getApplicationContext();
            this.userData.storeData(appContext);

            String paddedEncKey = CryptoUtils.get_aes_key(newPassword);
            SessionManager.updateAppSession(appContext, paddedEncKey);

            successStatus = true;
        }
        catch (Exception ex)
        {
            String msg = "Some issue occurred while updating your password.";
            if (showErrorMsg)
            {
                ToastUtils.showFailureMessage(this, msg + "\nPlease try again later.");
            }
        }

        return successStatus;
    }

    private boolean encryptAppDataWithNewKey(String newKey)
    {
        boolean successStatus = false;
        try
        {
            successStatus = new AllNotes(getApplicationContext()).encryptAllNotesWithNewKey(newKey);
        }
        catch (Exception ex)
        {
            // Exception Occurred
        }

        return successStatus;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        // char c = (char) event.getUnicodeChar(); // Gets pressed Key

        String password = this.newPasswordFld.getText().toString().trim();
        String reEnteredPassword = this.reenteredNewPasswordFld.getText().toString();

        if (password.length() == 0)
        {
            this.shapePw.getPaint().setColor(Color.BLACK);
        }
        else if (password.length() < 8)
        {
            this.shapePw.getPaint().setColor(Color.RED);
        }
        else
        {
            this.shapePw.getPaint().setColor(Color.GREEN);
        }

        if (! reEnteredPassword.equals(""))
        {
            if (reEnteredPassword.trim().equals(password))
            {
                this.shapeRePw.getPaint().setColor(Color.GREEN);
            }
            else
            {
                this.shapeRePw.getPaint().setColor(Color.RED);
            }
        }
        else
        {
            this.shapeRePw.getPaint().setColor(Color.BLACK);
        }

        // Set the border to the respective EditText fields
        this.newPasswordFld.setBackground(shapePw);
        this.reenteredNewPasswordFld.setBackground(shapeRePw);

        return super.onKeyUp(keyCode, event);
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(ChangePassword.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    public void onBackPressed()
    {
        this.goToNextPage(ToolSettings.class);
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
