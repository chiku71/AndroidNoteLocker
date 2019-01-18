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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SetSecretQuestion extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Spinner seqSpinner;
    private EditText seqAnswerField, writeOwnSqField;
    private ImageButton finishSignUpButton;
    private TextView pageHeader;
    private LinearLayout writeOwnSqLayout;

    String[] seqOptions = DataFileInfo.getSecretQuestions();
    String seqText, seqAnswerText, loginPasswordTxt;
    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_secret_question);

        this.loginPasswordTxt = getIntent().getStringExtra(Constants.PASSWORD_FOR_SIGNUP);
        this.initializeUIVariables();
        this.setUpActionListeners();

        //Adding the back button to the App Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpActionListeners()
    {
        this.seqSpinner.setOnItemSelectedListener(this);
        this.finishSignUpButton.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.seqSpinner = findViewById(R.id.seq_spinner);
        this.seqAnswerField = findViewById(R.id.seq_answer);
        this.finishSignUpButton = findViewById(R.id.get_started);
        this.pageHeader = findViewById(R.id.setup_security_qa_su);
        this.writeOwnSqField = findViewById(R.id.write_own_sq_ssqa);
        this.writeOwnSqLayout = findViewById(R.id.write_own_sq_layout_ssqa);

        this.pageHeader.setText(this.pageHeader.getText() + " " + EmojiUtils.getSmileyEmoji());

        ArrayAdapter arrayAdapterSeq = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.seqOptions);
        arrayAdapterSeq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.seqSpinner.setAdapter(arrayAdapterSeq);
    }

    private String getSecurityQuestion()
    {
        String securityQuestion = this.seqSpinner.getSelectedItem().toString();
        if (securityQuestion.equals(DataFileInfo.WRITE_YOUR_OWN_SECURITY_QUESTION_TXT))
        {
            securityQuestion = this.writeOwnSqField.getText().toString();
        }

        return securityQuestion;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.get_started)
        {
            String seqText = this.seqSpinner.getSelectedItem().toString();
            String seqAnswerText = this.seqAnswerField.getText().toString().trim();

            if (seqText.equals(this.seqOptions[0]))
            {
                ToastUtils.showFailureMessage(this, "Please select one secret question from the dropdown list.");
            }
            else if (seqText.equals(DataFileInfo.WRITE_YOUR_OWN_SECURITY_QUESTION_TXT) && this.writeOwnSqField.getText().toString().trim().length() == 0)
            {
                ToastUtils.showFailureMessage(this, "Please write your secret question.");
            }
            else if (seqAnswerText.length() == 0 )
            {
                ToastUtils.showFailureMessage(this, "Please provide answer for the selected secret question.");
            }
            else
            {
                this.seqText = this.getSecurityQuestion();
                this.seqAnswerText = seqAnswerText;

                boolean signUpCompletionStatus = this.completeSignUpProcess();
                if (signUpCompletionStatus)
                {
                    Intent userHomeActivity = new Intent(SetSecretQuestion.this, UserHome.class);
                    userHomeActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
                    userHomeActivity.putExtra(Constants.USER_PASSWORD, this.loginPasswordTxt);
                    userHomeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    SessionManager.createAppSession(getApplicationContext(), CryptoUtils.get_aes_key(this.loginPasswordTxt));
                    startActivity(userHomeActivity);
                }
                else
                {
                    ToastUtils.showFailureMessage(this, "Unable to complete Sign Up process due to some issue.\nPlease try again later ...");
                }
            }
        }
    }

    private void storeUserDataFromSignUp()
    {
        UserDataJson userDataObj = new UserDataJson(this.loginPasswordTxt, this.seqText, this.seqAnswerText);
        userDataObj.storeData(getApplicationContext());
    }

    private void storeAppDataFromSignUp(File newAppDataFile, Context appContext)
    {
        String welcomeNoteJsonTxt = getJsonTextForWelcomeNote(appContext);
        // 0 note sequence is reserved for Release note after update.
        String jsonTxtRaw = "{\n \"1\":" + welcomeNoteJsonTxt + "\n}";

        String encryptionKey = CryptoUtils.get_aes_key(this.loginPasswordTxt);
        String jsonTxtEnc = CryptoUtils.encryptString(encryptionKey, jsonTxtRaw);

        byte[] bytesText = jsonTxtEnc.getBytes();
        ToolUtils.writeDataIntoFile(bytesText, newAppDataFile);
    }

    private String getJsonTextForWelcomeNote(Context appContext)
    {
        String welcomeNoteDesc = ToolUtils.getTextFromAssetFile(appContext, DataFileInfo.getWelcomeNoteFileName());
        String currentTimeStamp = ToolUtils.getCurrentDateAndTime();

        NoteDetails welcomeNoteDetails = new NoteDetails();
        welcomeNoteDetails.setNoteHeader(Constants.WELCOME_NOTE_HEADER);
        welcomeNoteDetails.setNoteDesc(welcomeNoteDesc);
        welcomeNoteDetails.setNoteCreationTimestamp(currentTimeStamp);
        welcomeNoteDetails.setNoteLastModifiedTimestamp(currentTimeStamp);
        welcomeNoteDetails.setUserComment(Constants.WELCOME_NOTE_COMMENT);

        return welcomeNoteDetails.generateJsonString();
    }

    private boolean completeSignUpProcess()
    {
        Context appContext = getApplicationContext();
        File[] signUpDataFiles = ToolUtils.createSignUpDataFiles(appContext);
        File newUserDataFile = signUpDataFiles[0];
        File newAppDataFile = signUpDataFiles[1];
        File defaultSettingsConfigFile = signUpDataFiles[2];

        if (newUserDataFile != null && newAppDataFile != null && defaultSettingsConfigFile != null)
        {
            this.storeUserDataFromSignUp();
            this.storeAppDataFromSignUp(newAppDataFile, appContext);

            // Preparing new Settings Config file
            ToolSettingConfig defaultSettings = new ToolSettingConfig(getApplicationContext());
            defaultSettings.storeData();

            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
    {
        /* Check if User has opted for having their own security question.
         * If Yes, then show the EditText field for writing the Security Question.
         * Else if User has selected any other option Hide the EditText field for writing the Security Question.
         * */
        if (adapterView.getId() == R.id.seq_spinner)
        {
            String selectedValue = this.seqSpinner.getSelectedItem().toString();

            if (selectedValue.equals(DataFileInfo.WRITE_YOUR_OWN_SECURITY_QUESTION_TXT))
            {
                this.writeOwnSqLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                this.writeOwnSqLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        // Method to avoid error for method implementation for AdapterView.OnItemSelectedListener
    }

    @Override
    public void onBackPressed()
    {
        Intent signUpActivity = new Intent(SetSecretQuestion.this, SignUpActivity.class);
        signUpActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(signUpActivity);
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
    protected void onDestroy()
    {
        super.onDestroy();
        ToolUtils.closeApplication();
    }
}
