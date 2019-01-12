package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddCommentToNote extends NoteLockerAppActivity implements View.OnClickListener
{
    private TextView closingWindow;
    private EditText commentEditor;
    private Button saveComment;

    private int noteKey;
    private NoteDetails noteDetails;
    private String commentOnNote;
    private AllNotes allNotes;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment_to_note);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.allNotes = new AllNotes(appContext);

            this.initializeUIElements();
            this.setActionListeners();
            this.fetchAndSetValuesPassedFromPreviousAction();
            this.setUpPopUpUI();
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class);
        }
    }

    private void setUpPopUpUI()
    {
        DisplayMetrics displayMatrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMatrics);
        // getWindow().setLayout((int)(displayMatrics.widthPixels * 0.99), (int)(displayMatrics.heightPixels * 0.8));
        getWindow().setLayout(displayMatrics.widthPixels, displayMatrics.heightPixels);

        // Making the BackGround color of the popup window transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));//10% TRANSPARENT

        //Hiding the Action Bar from the UI
        getSupportActionBar().hide();
    }

    private void fetchAndSetValuesPassedFromPreviousAction()
    {
        this.noteKey = getIntent().getExtras().getInt(Constants.THIS_NOTE_KEY);
        this.commentOnNote = getIntent().getExtras().getString(Constants.NOTE_COMMENT);
        this.noteDetails = this.allNotes.getAllNoteDetails().get(this.noteKey);
        this.commentEditor.setText(this.commentOnNote);
    }

    private void addCommentToNote()
    {
        String modifiedComment = "";
        if (this.commentEditor.getText() != null)
        {
            modifiedComment = this.commentEditor.getText().toString();
        }
        this.noteDetails.setUserComment(modifiedComment);

        this.allNotes.updateNoteDetails(this.noteDetails, this.noteKey);
        this.allNotes.storeAllNotesInFile();
    }

    private void setActionListeners()
    {
        this.closingWindow.setOnClickListener(this);
        this.saveComment.setOnClickListener(this);
    }

    private void initializeUIElements()
    {
        this.closingWindow = findViewById(R.id.close_popup_add_comment);
        this.commentEditor = findViewById(R.id.write_note_comment);
        this.saveComment = findViewById(R.id.add_comment_to_note);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_comment_to_note:
                this.addCommentToNote();
                onBackPressed();
                break;
            case R.id.close_popup_add_comment:
                onBackPressed();
                break;
        }
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(AddCommentToNote.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }
}
