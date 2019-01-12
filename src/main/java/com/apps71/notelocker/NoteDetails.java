package com.apps71.notelocker;

import org.json.JSONObject;

public class NoteDetails extends  JsonDataFIle
{
    /**
     * Stores Note Details in a JSON File.
     * {
     *     "nh" : "Note Header Value",
     *     "nd" : "Note Description Value/Note Content",
     *     "nct" : "Note Creation Time Stamp",
     *     "nlmt" : "Note Last Modified Time Stamp",
     *     "cmt" : " Any Note Comment"
     * }
     */

    private String noteHeader;
    private String noteDesc;
    private String noteCreationTimestamp;
    private String noteLastModifiedTimestamp;
    private String userComment;
    private static String noteHeaderKey = Constants.NOTE_HEADER;
    private static String noteDescKey = Constants.NOTE_DESC;
    private static String noteCreationTSKey = Constants.NOTE_CREATION_TIMESTAMP;
    private static String noteLastModTSKey = Constants.NOTE_LAST_MODIFIED_TIMESTAMP;
    private static String noteCommentKey = Constants.NOTE_COMMENT;



    public NoteDetails()
    {
        this.noteHeader = this.getDefaultNoteHeader();
        this.noteDesc = "";
        this.noteCreationTimestamp = "";
        this.noteLastModifiedTimestamp = "";
        this.userComment = "";
    }

    public String generateJsonString()
    {
        String jsonString = "{\""+ noteHeaderKey +"\":\""+ this.noteHeader + "\",\"" + noteDescKey +"\":\"" +this.noteDesc +
                "\",\"" + noteCreationTSKey + "\":\"" + this.noteCreationTimestamp + "\",\"" + noteLastModTSKey + "\":\"" +
                this.noteLastModifiedTimestamp + "\",\"" + noteCommentKey + "\":\"" + this.userComment + "\"}";

        return jsonString;
    }

    public NoteDetails getNoteDetailsFromJsonObject(JSONObject noteDetailsJson)
    {
        try
        {
            this.noteHeader = noteDetailsJson.getString(noteHeaderKey);
            this.noteDesc = noteDetailsJson.getString(noteDescKey);
            this.noteCreationTimestamp = noteDetailsJson.getString(noteCreationTSKey);
            this.noteLastModifiedTimestamp = noteDetailsJson.getString(noteLastModTSKey);
            this.userComment = noteDetailsJson.getString(noteCommentKey);
        }
        catch (Exception exp)
        {
            // Error in getting note details from JSON Object
        }



        return this;
    }

    public String getBriefNoteDesc(int noteLength)
    {
        if (noteLength == 0)
        {
            noteLength = 60;
        }

        if (this.noteDesc.length() > noteLength)
        {
            return this.noteDesc.substring(0, noteLength).replaceAll("\n", " ");
        }
        else
        {
            return this.noteDesc.replaceAll("\n", " ");
        }
    }

    //Overloaded method
    public String getBriefNoteDesc()
    {
        return getBriefNoteDesc(0);
    }

    @Override
    public String toString()
    {
        return "NoteDetails( Header : " + this.noteHeader + ", " +
                "Desc : " + this.getBriefNoteDesc(15) + ", " +
                "Created On : " + this.noteCreationTimestamp + ", " +
                "Last Modified On : " + this.noteLastModifiedTimestamp + ", " +
                "Comment : " + this.userComment + " )";
    }


    private String getDefaultNoteHeader()
    {
        return Constants.DEFAULT_NOTE_HEADER;
    }


    public String getNoteHeader()
    {
        if (this.noteHeader == null || this.noteHeader.trim().equals(""))
        {
            this.noteHeader = getDefaultNoteHeader();
        }
        return this.noteHeader;
    }

    public void setNoteHeader(String noteHeader)
    {
        if (noteHeader == null || noteHeader.trim().equals(""))
        {
            noteHeader = getDefaultNoteHeader();
        }
        this.noteHeader = noteHeader;
    }

    public String getNoteDesc() {
        return filterNull(noteDesc);
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = filterNull(noteDesc);
    }

    public String getNoteCreationTimestamp() {
        return filterNull(noteCreationTimestamp);
    }

    public void setNoteCreationTimestamp(String noteCreationTimestamp)
    {
        this.noteCreationTimestamp = filterNull(noteCreationTimestamp);
    }

    public String getNoteLastModifiedTimestamp() {
        return filterNull(noteLastModifiedTimestamp);
    }

    public void setNoteLastModifiedTimestamp(String noteLastModifiedTimestamp)
    {
        this.noteLastModifiedTimestamp = filterNull(noteLastModifiedTimestamp);
    }

    public String getUserComment() {
        return filterNull(userComment);
    }

    public void setUserComment(String userComment)
    {
        this.userComment = filterNull(userComment);
    }

    private String filterNull(String input)
    {
        if (input == null)
        {
            input = "";
        }
        return input;
    }
}
