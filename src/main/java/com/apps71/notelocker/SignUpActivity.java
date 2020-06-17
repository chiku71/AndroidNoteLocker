package com.apps71.notelocker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText signUpPasswordField, reEnteredPasswordField;
    ImageButton setPasswordButton;
    ShapeDrawable shapePw, shapeRePw;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.initializeUIElements();
        this.setUpActionListeners();

        this.initiateShapesForPasswordFields();

        //Adding the back button to the App Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initializeUIElements()
    {
        this.signUpPasswordField = findViewById(R.id.password_new);
        this.reEnteredPasswordField = findViewById(R.id.password_reentered);
        this.setPasswordButton = findViewById(R.id.set_password);
    }

    private void setUpActionListeners()
    {
        this.setPasswordButton.setOnClickListener(this);
    }

    private void initiateShapesForPasswordFields()
    {
        this.shapePw = UiEditNoteUtils.getPasswordFieldShape();
        this.shapeRePw = UiEditNoteUtils.getPasswordFieldShape();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.set_password:
                String password = this.signUpPasswordField.getText().toString().trim();
                String reEnteredPassword = this.reEnteredPasswordField.getText().toString().trim();

                if (password.length() >= 8)
                {
                    if (password.equals(reEnteredPassword))
                    {
                        Intent setSeqActivity = new Intent(SignUpActivity.this, SetSecretQuestion.class);
                        setSeqActivity.putExtra(Constants.FROM_ACTIVITY,this.className);
                        setSeqActivity.putExtra(Constants.PASSWORD_FOR_SIGNUP, password);
                        startActivity(setSeqActivity);
                    }
                    else
                    {
                        ToastUtils.showFailureMessage(this, "Password from both fields didn't match.\nPlease try again ...");
                    }
                }
                else
                {
                    ToastUtils.showFailureMessage(this, "Password should be of at least 8 characters.\nPlease try again ...");
                }
                break;
        }
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event)
    {
        // char c = (char) event.getUnicodeChar();

        String password = this.signUpPasswordField.getText().toString().trim();
        String reEnteredPassword = this.reEnteredPasswordField.getText().toString();

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
        this.signUpPasswordField.setBackground(shapePw);
        this.reEnteredPasswordField.setBackground(shapeRePw);

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed()
    {
        Intent newInstallationActivity = new Intent(SignUpActivity.this, NewInstallationRunner.class);
        newInstallationActivity.putExtra(Constants.FROM_ACTIVITY, this.className);

        startActivity(newInstallationActivity);
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
