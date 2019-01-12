package com.apps71.notelocker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class AboutNotePopup extends NoteLockerAppActivity implements View.OnClickListener
{
    private TextView noteHeader, noteCreationTS, noteLastModTs, noteComment, closePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_note_popup);

        this.initiateUIComponents();
        this.setUpPopUpUI();
        this.populateValuesToUI();
        this.setActionListeners();
    }

    private void setUpPopUpUI()
    {
        DisplayMetrics displayMatrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMatrics);
        //getWindow().setLayout((int)(displayMatrics.widthPixels * 0.99), (int)(displayMatrics.heightPixels * 0.8));
        getWindow().setLayout(displayMatrics.widthPixels, displayMatrics.heightPixels);

        // Making the BackGround color of the popup window transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));//10% TRANSPARENT

        //Hiding the Action Bar from the UI
        getSupportActionBar().hide();
    }

    private void initiateUIComponents()
    {
        this.closePopup = findViewById(R.id.close_popup_about_note);
        this.noteHeader = findViewById(R.id.note_header_about_note);
        this.noteCreationTS = findViewById(R.id.node_creation_date_about_note);
        this.noteLastModTs = findViewById(R.id.note_last_mod_date_about_note);
        this.noteComment = findViewById(R.id.comment_about_note);
    }

    private void setActionListeners()
    {
        this.closePopup.setOnClickListener(this);
    }

    private void populateValuesToUI()
    {
        this.noteHeader.setText(getIntent().getExtras().getString(Constants.NOTE_HEADER));
        this.noteCreationTS.setText(getIntent().getExtras().getString(Constants.NOTE_CREATION_TIMESTAMP));
        this.noteLastModTs.setText(getIntent().getExtras().getString(Constants.NOTE_LAST_MODIFIED_TIMESTAMP));
        this.noteComment.setText(getIntent().getExtras().getString(Constants.NOTE_COMMENT));

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.close_popup_about_note:
                this.onBackPressed();
                break;
        }
    }
}
