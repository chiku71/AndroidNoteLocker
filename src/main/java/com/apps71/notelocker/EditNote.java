package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class EditNote extends NoteLockerAppActivity implements View.OnClickListener, View.OnFocusChangeListener
{
    private EditText noteTitle, noteDesc;
    private TextView pageInfo;
    private FloatingActionButton saveNote;

    private NoteDetails noteDetails;
    private boolean isNewNote = false;
    private int noteKey;
    private AllNotes allNotes;

    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            // App session has been authenticated successfully.
            this.allNotes = new AllNotes(getApplicationContext());

            this.initializeUIVariables();
            this.checkForNewNote();
            this.setUpNoteForEdit();
            this.setUpActionListeners();

            // Setting up the custom tool bar | Not required now as the original App bar is being used now
            // Toolbar userHomeToolBar = findViewById(R.id.edit_note_toolbar);
            //setSupportActionBar(userHomeToolBar);

            //Adding the back button to the App Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class);
        }
    }

    private void setUpActionListeners()
    {
        noteTitle.setOnFocusChangeListener(this);
        this.saveNote.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.noteTitle = findViewById(R.id.edit_note_title);
        this.noteDesc = findViewById(R.id.edit_note_desc);
        this.pageInfo = findViewById(R.id.page_info_edit_or_new);
        this.saveNote = findViewById(R.id.save_note_floating);
    }

    private void setUpNoteForEdit()
    {
        if (this.isNewNote)
        {
            this.pageInfo.setText(Constants.NEW_NOTE_TITLE);
            this.noteDetails = new NoteDetails();
            this.noteDetails.setNoteCreationTimestamp(ToolUtils.getCurrentDateAndTime());
        }
        else
        {
            this.pageInfo.setText(Constants.EDIT_NOTE_TITLE);
            this.noteKey = getIntent().getExtras().getInt(Constants.THIS_NOTE_KEY);
            this.noteDetails = this.allNotes.getAllNoteDetails().get(this.noteKey);

            this.noteTitle.setText(this.noteDetails.getNoteHeader());
            this.noteDesc.setText(this.noteDetails.getNoteDesc());
        }
    }

    private void checkForNewNote()
    {
        if (getIntent().getExtras().getString(Constants.NOTE_TYPE).equals(Constants.NEW))
        {
            this.isNewNote = true;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        View separator = findViewById(R.id.separator_edit_note);
        if (view.getId() == R.id.edit_note_title && hasFocus)
        {
            separator.setVisibility(View.INVISIBLE);
        }
        else
        {
            separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.save_note_floating:
                this.saveNoteDetails();
                this.onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
            if (this.isNewNote)
            {
                this.goToNextPage(UserHome.class);
            }
            else
            {
                try
                {
                    Intent readNoteActivity = new Intent(EditNote.this, ReadNote.class);
                    readNoteActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
                    readNoteActivity.putExtra(Constants.THIS_NOTE_KEY, this.noteKey);
                    startActivity(readNoteActivity);
                }
                catch (Exception ex)
                {
                    String toastMsg = "Some issue occurred.\nRedirecting to Home page.";
                    ToastUtils.showWarningMessage(getApplicationContext(), toastMsg);
                    this.goToNextPage(UserHome.class);
                }

            }
    }

    private void goToNextPage(Class nextClass)
    {
        Intent nextPageActivity = new Intent(EditNote.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(nextPageActivity);
    }

    private void saveNoteDetails()
    {
        // Fetching data from UI
        String noteHeader = this.noteTitle.getText().toString();
        String noteDescription = this.noteDesc.getText().toString();
        String lastModified = ToolUtils.getCurrentDateAndTime();

        //Setting up details to NoteDetails object
        this.noteDetails.setNoteHeader(noteHeader);
        this.noteDetails.setNoteDesc(noteDescription);
        this.noteDetails.setNoteLastModifiedTimestamp(lastModified);

        if (this.isNewNote)
        {
            this.allNotes.addNoteDetails(this.noteDetails);
        }
        else
        {
            this.allNotes.updateNoteDetails(this.noteDetails, this.noteKey);
        }

        this.allNotes.storeAllNotesInFile();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_note_menu:
                this.saveNoteDetails();
                this.onBackPressed();
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }
}
