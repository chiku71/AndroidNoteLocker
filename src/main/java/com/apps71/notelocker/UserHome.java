package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class UserHome extends NoteLockerAppActivity implements View.OnClickListener
{

    private String className = ToolUtils.getClassNameFromObject(this);
    private static Map<Integer, Integer> pageKeyIdMap = null;
    boolean backButtonPressedTwice = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        FloatingActionButton addNoteFloatingButton = findViewById(R.id.add_new_note_floating);

        pageKeyIdMap = new TreeMap<>();
        Context appContext = getApplicationContext();

        if (SessionManager.authenticateSession(appContext))
        {
            AllNotes allNotes = new AllNotes(appContext);
            // App session has been authenticated successfully.
            Map<Integer, NoteDetails> allNoteDetails = allNotes.getAllNoteDetails();

            if (allNoteDetails == null)
            {
                // Loading all Notes ...
                allNoteDetails = allNotes.fetchAllNoteDetails();
            }

            this.displayAllNotesInUserHome(allNoteDetails);

            // Note : As we are using the default App Bar, we don't need any custom tool bar.
            // Toolbar userHomeToolBar = findViewById(R.id.user_home_toolbar);
            // setSupportActionBar(userHomeToolBar);

            addNoteFloatingButton.setOnClickListener(this);
        }
        else
        {
            // App Session authentication failed.
            this.goToNextPage(LoginActivity.class, true);
        }
    }

    private View addNewSeparator(RelativeLayout layout, int upperElementId)
    {
        return LayoutUtils.addViewToLayout(this, layout, upperElementId,
                RelativeLayout.LayoutParams.MATCH_PARENT,5, getResources().getColor(R.color.separatorColor));
    }

    private LinearLayout createNoteDetailsLayout(NoteDetails thisNote, int previousElementId)
    {
        RelativeLayout.LayoutParams layoutParamLinearLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        layoutParamLinearLayout.addRule(RelativeLayout.BELOW, previousElementId);

        linearLayout.setId(LinearLayout.generateViewId());
        linearLayout.setLayoutParams(layoutParamLinearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutUtils.addTextViewToLayout(this, linearLayout, layoutParams, thisNote.getNoteHeader(), Color.WHITE, 30);
        LayoutUtils.addTextViewToLayout(this, linearLayout, layoutParams, thisNote.getBriefNoteDesc(), Color.GRAY, 12);
        LayoutUtils.addTextViewToLayout(this, linearLayout, layoutParams,
                "Last Modified : " + thisNote.getNoteLastModifiedTimestamp(),
                Color.BLUE, 15);

        return linearLayout;
    }


    private void displayAllNotesInUserHome(Map<Integer, NoteDetails> allNotesMap)
    {
        Set<Integer> keySet = allNotesMap.keySet();

        RelativeLayout userHomeLayout = findViewById(R.id.layout_user_home);
        LinearLayout allNotesScrollView = findViewById(R.id.linear_layout_all_notes);

        //Adding new separator below the Page Title
        View primarySeparator = addNewSeparator(userHomeLayout, R.id.page_info_user_home);
        int previousElementID =  primarySeparator.getId();

        for (Integer key:keySet)
        {
            NoteDetails thisNote = allNotesMap.get(key);

            //Creating a Layout to View Note Details in home page.
            LinearLayout linearLayout = this.createNoteDetailsLayout(thisNote, previousElementID);
            //userHomeLayout.addView(linearLayout);
            allNotesScrollView.addView(linearLayout);
            //Creating a map of Layout ID as key and the AllNotesMap key as value.
            pageKeyIdMap.put(linearLayout.getId(), key);
            //Setting On Click Listener for the Linear Layout to go to Read Note page.
            linearLayout.setOnClickListener(this);

            //Adding a separator below the this Note Details Layout
            View newSeparator = this.addNewSeparator(userHomeLayout, linearLayout.getId());
            previousElementID = newSeparator.getId();
        }
    }


    private void checkForClickOnNoteDetailsLayouts(int viewId)
    {
        Set<Integer> allNoteDetailsLayoutIds = pageKeyIdMap.keySet();
        if (allNoteDetailsLayoutIds.contains(viewId))
        {
            int keyForNoteDetails = pageKeyIdMap.get(viewId);
            //NoteDetails clickedNoteDetails = AllNotes.allNoteDetails.get(keyForNoteDetails);

            Intent readNoteActivity = new Intent(UserHome.this, ReadNote.class);
            readNoteActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
            readNoteActivity.putExtra(Constants.THIS_NOTE_KEY, keyForNoteDetails);
            startActivity(readNoteActivity);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_new_note_floating:
                this.moveToEditNoteActivity(true);
                break;
            default:
                this.checkForClickOnNoteDetailsLayouts(view.getId());
                break;
        }
    }

    private void moveToEditNoteActivity(boolean isNewNote)
    {
        String noteType = Constants.EDIT;
        if (isNewNote)
        {
            noteType = Constants.NEW;
        }

        Intent editActivity = new Intent(UserHome.this, EditNote.class);
        editActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        editActivity.putExtra(Constants.NOTE_TYPE, noteType);
        startActivity(editActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                this.goToNextPage(ToolSettings.class, false);
                break;

            case R.id.backup_data:
                this.goToNextPage(BackUpData.class, false);
                break;

            case R.id.about_us:
                this.goToNextPage(About.class, false);
                break;

            case R.id.logout:
                this.logoutApplication();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToNextPage(Class nextClass, boolean clearBackStacks)
    {
        Intent nextPageActivity = this.prepareIntent(nextClass, clearBackStacks);
        startActivity(nextPageActivity);
    }

    private Intent prepareIntent(Class nextClass, boolean clearBackStacks)
    {
        Intent nextPageActivity = new Intent(UserHome.this, nextClass);
        nextPageActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        if (clearBackStacks)
        {
            nextPageActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        return nextPageActivity;
    }

    private void logoutApplication()
    {
        SessionManager.endAppSession(getApplicationContext());
        moveTaskToBack(true);
        this.finish();
//        Intent logoutActivity =  this.prepareIntent(LoginActivity.class, true);
//        logoutActivity.putExtra(Constants.EXIT_APPLICATION, true);
//        startActivity(logoutActivity);
    }

    @Override
    public void onBackPressed()
    {
        if (this.backButtonPressedTwice)
        {
            // Back button has been pressed twice ...
            this.logoutApplication();
            return;
        }

        this.backButtonPressedTwice = true;
        ToastUtils.showInfoForShortTime(this, "Press the Back button again to Log out.");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                backButtonPressedTwice=false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        SessionManager.endAppSession(getApplicationContext());
        ToolUtils.closeApplication();
    }
}
