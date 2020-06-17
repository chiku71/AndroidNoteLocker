package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class LockApplication extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton unlockBtn;
    private EditText enteredPasswordFld;

    private boolean enteredPasswordAuthenticated = false;
    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Prevent this page from coming to front and interrupting other ongoing activity of user.
        // moveTaskToBack(true);

        // Prepare the Activity UI.
        setContentView(R.layout.activity_lock_application);

        this.initializeUIVariables();
        this.setUpActionListeners();
    }

    private void setUpActionListeners()
    {
        this.unlockBtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.unlockBtn = findViewById(R.id.unlock_btn_ls);
        this.enteredPasswordFld = findViewById(R.id.unlock_password_ls);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.unlock_btn_ls:
                this.authenticateCredentials();
                break;
        }
    }

    private void authenticateCredentials()
    {
        String enteredPassword  = this.enteredPasswordFld.getText().toString().trim();

        if (enteredPassword.length() == 0)
        {
            ToastUtils.showFailureMessage(this, "Please enter your password");
        }
        else if (enteredPassword.length() < 8)
        {
            ToastUtils.showFailureMessage(this, "Please enter a valid password.\nHint: Password contains at least eight characters.");
        }
        else
        {
            Context appContext = getApplicationContext();
            UserDataJson userData = new UserDataJson(appContext);
            String userPassword = userData.getPassword();

            if (enteredPassword.trim().equals(userPassword))
            {
                // Entered password matched ...
                this.enteredPasswordAuthenticated = true;
                this.unlockApplication();
                this.enteredPasswordAuthenticated = false;
            }
            else
            {
                ToastUtils.showFailureMessage(this, "Incorrect password.\nPlease try again ...");
            }
        }

    }

    private void unlockApplication()
    {
        try
        {
            this.onBackPressed();
        }
        catch (Exception ex)
        {
            // Couldn't proceed to the previous Activity with BackPress
            String toastMsg = "Some issue occurred while unlocking the application.\nRedirecting to Home page.";
            ToastUtils.showWarningMessage(getApplicationContext(), toastMsg);

            this.goToNextPage(UserHome.class);
        }
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(LockApplication.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    public void onBackPressed()
    {
        if (this.enteredPasswordAuthenticated)
        {
            super.onBackPressed();
        }
    }
}
