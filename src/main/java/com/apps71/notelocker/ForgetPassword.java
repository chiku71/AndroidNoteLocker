package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ForgetPassword extends AppCompatActivity implements View.OnClickListener
{
    private TextView securityQuestion, outputMessage;
    private EditText usersAnswer;
    private Button showPassword;

    private UserDataJson userDataJson;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        this.setClassVariables();
        this.initializeUIVariables();
        this.setUpSecurityQuestion();
        this.setUpActionListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setClassVariables()
    {
        Context appContext = getApplicationContext();
        this.userDataJson = new UserDataJson(appContext);
    }

    private void setUpActionListeners()
    {
        this.showPassword.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.securityQuestion = findViewById(R.id.seq_for_user);
        this.outputMessage = findViewById(R.id.output_message_forget_password);
        this.usersAnswer = findViewById(R.id.seq_answer_user);
        this.showPassword = findViewById(R.id.show_password);
    }

    private void setUpSecurityQuestion()
    {
        String securityQuestion = this.userDataJson.getSecurityQuestion();
        this.securityQuestion.setText(securityQuestion);
    }

    @Override
    public void onBackPressed()
    {
        Intent loginActivity = new Intent(ForgetPassword.this, LoginActivity.class);
        loginActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivity);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.show_password)
        {
            // Set output message to blank first.
            this.outputMessage.setText("");
            String answerProvidedByUser = this.usersAnswer.getText().toString().trim();

            if (answerProvidedByUser.equals(this.userDataJson.getSecurityAnswer()))
            {
                String msg = "Your password is : ";
                String userPassword = "<font color='#0C1481'>" + this.userDataJson.getPassword() + "</font>";
                this.outputMessage.setText(Html.fromHtml(msg + "'" + userPassword + "'."));
                ToastUtils.showSuccessMessage(this, "Security details validated successfully ...");
            }
            else
            {
                String errorMsg = "Your answer didn't match.\nPlease try again ...";
                ToastUtils.showFailureMessage(this, errorMsg);
            }
        }

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
}
