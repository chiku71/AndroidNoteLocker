package com.apps71.notelocker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class NewInstallationRunner extends AppCompatActivity implements View.OnClickListener
{
    /***
     * This activity is for new installation of this application for user to decide :-
     *      -> to SignUp into the application.
     *      or -> to import BackUp files from previous installation.
     *          (For this user needs to remember the old credentials(Either password or security answer)
     *              so that tool can authenticate the user's identity.)
     */

    // Declaring Class variables
    ImageButton proceedToSignUpBtn, proceedToImportBackUpBtn;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_installation_runner);

        this.initializeUIVariables();
        this.setUpActionListeners();
    }

    private void setUpActionListeners()
    {
        this.proceedToSignUpBtn.setOnClickListener(this);
        this.proceedToImportBackUpBtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.proceedToSignUpBtn = findViewById(R.id.proceed_to_signup_new_installation);
        this.proceedToImportBackUpBtn = findViewById(R.id.proceed_to_import_backup_file_new_installation);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.proceed_to_signup_new_installation:
                this.goToNextPage(SignUpActivity.class);
                break;

            case R.id.proceed_to_import_backup_file_new_installation:
                this.goToNextPage(RestoreBackUpFileActivity.class);
                break;
        }
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(NewInstallationRunner.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}
