package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class ChangeSecurityQA extends NoteLockerAppActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    ImageButton updateSecurityQABtn;
    Spinner seqSpinner;
    EditText enteredPasswordFld, enteredSeqAnsFld;

    String[] seqOptions = DataFileInfo.getSecretQuestions();
    String seqText, seqAnswerText, enteredPassword;
    UserDataJson userData;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_security_q);

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
        this.seqSpinner.setOnItemSelectedListener(this);
        this.updateSecurityQABtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.enteredPasswordFld = findViewById(R.id.password_usqa);
        this.seqSpinner = findViewById(R.id.seq_spinner_usqa);
        this.enteredSeqAnsFld = findViewById(R.id.seq_ans_usqa);
        this.updateSecurityQABtn = findViewById(R.id.update_security_qa_usqa);


        ArrayAdapter arrayAdapterSeq = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.seqOptions);
        arrayAdapterSeq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.seqSpinner.setAdapter(arrayAdapterSeq);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.update_security_qa_usqa)
        {
            this.processInputData();
        }
    }

    private void  processInputData()
    {
        this.seqText = this.seqSpinner.getSelectedItem().toString();
        this.seqAnswerText = this.enteredSeqAnsFld.getText().toString().trim();
        this.enteredPassword = this.enteredPasswordFld.getText().toString().trim();

        if (this.enteredPassword.length() == 0)
        {
            ToastUtils.showFailureMessage(this, "Please enter current password.");
        }
        else if (this.enteredPassword.length() < 8)
        {
            ToastUtils.showFailureMessage(this, "\"Please enter a valid password.\nHint: Password contains at least eight characters.");
        }
        else if (this.seqText.equals(this.seqOptions[0]))
        {
            ToastUtils.showFailureMessage(this, "Please select one secret question from the dropdown list.");
        }
        else if (this.seqAnswerText.length() == 0 )
        {
            ToastUtils.showFailureMessage(this, "Please provide answer for the selected secret question.");
        }
        else
        {
            String userPassword = this.userData.getPassword();
            if (userPassword.equals(this.enteredPassword))
            {
                try
                {
                    this.updateSecurityQA();
                    ToastUtils.showSuccessMessage(this, "Your Security Q&A has been updated successfully.");
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

    private void updateSecurityQA()
    {
        this.userData.setSecurityQuestion(this.seqText);
        this.userData.setSecurityAnswer(this.seqAnswerText);
        this.userData.storeData(getApplicationContext());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
    {
       // Method to override the Abstract method to overcome compilation error
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        // Method to avoid error for method implementation for AdapterView.OnItemSelectedListener
    }

    @Override
    public void onBackPressed()
    {
        this.goToNextPage(ToolSettings.class);
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(ChangeSecurityQA.this, nextClass);
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
