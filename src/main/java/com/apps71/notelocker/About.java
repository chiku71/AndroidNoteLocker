package com.apps71.notelocker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class About extends NoteLockerAppActivity implements View.OnClickListener
{
    private TextView versionNameFld;
    private Button sendFeedbackBtn;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.initializeUIElements();
        this.setUpActionListeners();

        //Adding the back button to the App Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeUIElements()
    {
        this.sendFeedbackBtn = findViewById(R.id.send_feedback_about);
        this.versionNameFld = findViewById(R.id.tool_version_name_about);

        // Add version number from Package Info.
        String existingTxtVersionName = this.versionNameFld.getText().toString();
        this.versionNameFld.setText(existingTxtVersionName + PackageUtils.getVersionName(getApplicationContext()));
    }

    private void setUpActionListeners()
    {
        this.sendFeedbackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.send_feedback_about:
                this.openEmailClientForSendingFeedback();
                break;
        }
    }

    private void openEmailClientForSendingFeedback()
    {
        String subject = "FEEDBACK: NoteLocker Android App";
        String body = "";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:chiku71@gmail.com?subject=" + subject + "&body=" + body);
        intent.setData(data);
        startActivity(intent);
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
    public void onBackPressed()
    {
        this.goToNextPage(UserHome.class);
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(About.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }
}
