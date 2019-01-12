package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ReadNote extends NoteLockerAppActivity implements View.OnClickListener
{
    private TextView noteHeader, noteDesc;
    private FloatingActionButton editButtonFloating;

    private int thisNoteKey;
    private NoteDetails thisNoteDetails;
    private AllNotes allNotes;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // Session authentication successful
            this.allNotes = new AllNotes(appContext);

            //Fetching the Note Key pass from previous activity to get Note Details.
            this.thisNoteKey = getIntent().getExtras().getInt(Constants.THIS_NOTE_KEY);
            this.thisNoteDetails = this.allNotes.getAllNoteDetails().get(this.thisNoteKey);

            this.initializeUIElements();
            this.setUpActionListeners();
            this.showNoteDetailsInUI();

            //Adding the back button to the App Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class, false);
        }
    }


    private void showNoteDetailsInUI()
    {
        this.noteHeader.setText(this.thisNoteDetails.getNoteHeader());
        this.noteDesc.setText(this.thisNoteDetails.getNoteDesc());
    }

    private void initializeUIElements()
    {
        this.noteHeader = findViewById(R.id.note_header_read_note);
        this.noteDesc = findViewById(R.id.note_desc_read_note);
        this.editButtonFloating = findViewById(R.id.edit_note_floating);

        // Enabling scrolling to the Note Description
        this.noteDesc.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setUpActionListeners()
    {
        this.editButtonFloating.setOnClickListener(this);
    }

    private void moveToEditNoteActivity(boolean isNewNote)
    {
        String noteType = Constants.EDIT;
        if (isNewNote)
        {
            noteType = Constants.NEW;
        }

        Intent editActivity = new Intent(ReadNote.this, EditNote.class);
        editActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        editActivity.putExtra(Constants.NOTE_TYPE, noteType);
        editActivity.putExtra(Constants.THIS_NOTE_KEY, this.thisNoteKey);
        startActivity(editActivity);
    }

    private void moveToAboutNoteActivity()
    {
        Intent aboutNoteActivity = new Intent(ReadNote.this, AboutNotePopup.class);
        aboutNoteActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        aboutNoteActivity.putExtra(Constants.NOTE_HEADER, this.thisNoteDetails.getNoteHeader());
        aboutNoteActivity.putExtra(Constants.NOTE_CREATION_TIMESTAMP, this.thisNoteDetails.getNoteCreationTimestamp());
        aboutNoteActivity.putExtra(Constants.NOTE_LAST_MODIFIED_TIMESTAMP, this.thisNoteDetails.getNoteLastModifiedTimestamp());
        aboutNoteActivity.putExtra(Constants.NOTE_COMMENT, this.thisNoteDetails.getUserComment());
        startActivity(aboutNoteActivity);
    }

    private void deleteThisNote()
    {
        this.allNotes.deleteNoteDetails(this.thisNoteKey);
        this.allNotes.storeAllNotesInFile();

        // Now go back to User Home Page
        this.onBackPressed();
    }

    private void prepareForAddingComment()
    {
        Intent addCommentActivity = new Intent(ReadNote.this, AddCommentToNote.class);
        addCommentActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        addCommentActivity.putExtra(Constants.THIS_NOTE_KEY, this.thisNoteKey);
        addCommentActivity.putExtra(Constants.NOTE_COMMENT, this.thisNoteDetails.getUserComment());
        startActivity(addCommentActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.edit_note_menu:
                this.moveToEditNoteActivity(false);
                break;
            case R.id.other_note_details_read:
                this.moveToAboutNoteActivity();
                break;
            case R.id.delete_note_read_menu:
                this.deleteThisNote();
                break;
            case R.id.add_comment_read:
                this.prepareForAddingComment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void goToNextPage(Class nextClass, boolean removeOldActivityFromStack)
    {
        Intent nextPageActivity = new Intent(ReadNote.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        if (removeOldActivityFromStack)
        {
            nextPageActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(nextPageActivity);
    }


    @Override
    public void onBackPressed()
    {
        this.goToNextPage(UserHome.class, false);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.edit_note_floating:
                this.moveToEditNoteActivity(false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_read_note, menu);
        return true;
    }
}
