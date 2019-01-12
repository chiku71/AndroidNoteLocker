package com.apps71.notelocker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton loginButton;
    private TextView forgetPassword;
    private EditText enteredPassword;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        this.initializeUIVariables();
        this.setUpActionListeners();

        if(getIntent().getBooleanExtra(Constants.EXIT_APPLICATION, false))
        {
            this.onBackPressed();
        }
    }

    private void setUpActionListeners()
    {
        this.loginButton.setOnClickListener(this);
        this.forgetPassword.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.loginButton = findViewById(R.id.Login);
        this.forgetPassword = findViewById(R.id.forget_password);
        this.enteredPassword = findViewById(R.id.loginPassword);
    }

    private void authenticateCredentials()
    {
        String enteredPassword  = this.enteredPassword.getText().toString().trim();

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

                SessionManager.createAppSession(appContext, CryptoUtils.get_aes_key(userPassword));
                Intent userHomeActivity = new Intent(LoginActivity.this, UserHome.class);
                userHomeActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
                userHomeActivity.putExtra(Constants.USER_PASSWORD, userPassword);
                userHomeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //Intent.FLAG_ACTIVITY_NEW_TASK |

                this.enteredPassword.setText(null);
                startActivity(userHomeActivity);
            }
            else
            {
                ToastUtils.showFailureMessage(this, "Incorrect password.\nPlease try again ...");
            }
        }
    }

    private void goToForgetPasswordPage()
    {
        Intent forgetPasswordActivity = new Intent(LoginActivity.this, ForgetPassword.class);
        forgetPasswordActivity.putExtra(Constants.FROM_ACTIVITY, this.className);

        this.enteredPassword.setText(null);
        startActivity(forgetPasswordActivity);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.Login:
                this.authenticateCredentials();
                break;

            case R.id.forget_password:
                this.goToForgetPasswordPage();
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ToolUtils.closeApplication();
    }

    @Override
    public void onBackPressed()
    {
        // Back button pressed from Login Activity ...
        moveTaskToBack(true);
        this.finish();
        this.onDestroy();
        ToolUtils.closeApplication();
    }

}
