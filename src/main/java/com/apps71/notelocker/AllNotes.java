package com.apps71.notelocker;
import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class AllNotes extends JsonDataFIle
{
    /**
     * Stores all Note Details in a JSON File
     * {
         "0":
            {
                 "nh" : "MyNote",
                 "nd" : "My Note Desc. \n My Note Desc Line 2.",
                 "nct" : "01-Jan-2018",
                 "nlmt" : "",
                 "cmt": "This Note is created for testing."
             },
        }
     *
     * Reserved Sequences
     * -------------------------------------------------------------
     * "0" - note sequence is reserved for Release note after update.
     * "1" - note sequence is used for Welcome Note for new installation.
     * User Notes will start from sequence 2.
     *
     *
     */

    // All Notes are stored in this allNoteDetails after successful login in a static way.
    private static Map<Integer, NoteDetails> allNoteDetails = null;


    private Context appContext;
    private boolean isSessionAuthenticated;

    public AllNotes(Context appContext)
    {
        this.appContext = appContext;
        this.isSessionAuthenticated = this.runSessionAuthentication();
    }

    private boolean runSessionAuthentication()
    {
        if (SessionManager.authenticateSession(this.appContext))
        {
            return true;
        }

        return false;
    }

    @Override
    protected String generateJsonString()
    {
        if (this.isSessionAuthenticated && allNoteDetails != null)
        {
            StringBuffer jsonData = new StringBuffer("{\n");
            Set<Integer> keySet = allNoteDetails.keySet();


            int keySetSize = keySet.size();
            Integer[] allNoteKeys = keySet.toArray(new Integer[keySetSize]);

            for (int i=0; i< keySetSize; i++)
            {
                Integer key = allNoteKeys[i];
                String jsonNoteDetails = allNoteDetails.get(key).generateJsonString();
                String newNote = "\"" + key + "\" : " + jsonNoteDetails;
                if (i != allNoteDetails.size() - 1)
                {
                    newNote = newNote + ",";
                }
                jsonData.append(newNote);
            }

            jsonData.append("\n}");
            return jsonData.toString();
        }

        return "{}";
    }

    protected void addNoteDetails(NoteDetails noteDetails)
    {
        if (this.isSessionAuthenticated)
        {
            if (allNoteDetails == null)
            {
                this.initializeAllNotes();
            }

            Set<Integer> keySet = allNoteDetails.keySet();

            // For empty Note Set the NoteKey starts from 2 for User Notes as 0 and 1 are reserved.
            int nextKey = 2;

            if (!keySet.isEmpty())
            {
                nextKey = Collections.max(keySet) + 1;
            }

            allNoteDetails.put(nextKey, noteDetails);
        }
    }

    private void initializeAllNotes()
    {
        allNoteDetails = new TreeMap<>();
    }

    protected void deleteNoteDetails(int noteKey)
    {
        if (this.isSessionAuthenticated && allNoteDetails != null && allNoteDetails.containsKey(noteKey))
        {
            allNoteDetails.remove(noteKey);
        }
    }

    protected void updateNoteDetails(NoteDetails noteDetails, int noteKey)
    {
        if (this.isSessionAuthenticated)
        {
            if (allNoteDetails == null)
            {
                this.initializeAllNotes();
            }
            allNoteDetails.put(noteKey, noteDetails);
        }
    }

    protected void storeAllNotesInFile()
    {
        if (this.isSessionAuthenticated)
        {
            String allNotesJsonTxt = this.generateJsonString();
            File appDataFile = ToolUtils.getExpectedAppDataFile(appContext);

            //Encrypt the JSON Text
            String encryptionKey = SessionManager.getAppDataEncryptionKey(appContext);
            String jsonTxtEnc = CryptoUtils.encryptString(encryptionKey, allNotesJsonTxt);

            byte[] bytesText = jsonTxtEnc.getBytes();
            ToolUtils.writeDataIntoFile(bytesText, appDataFile);
        }
    }

    protected boolean encryptAllNotesWithNewKey(String newEncKey)
    {
        boolean successStatus = false;
        if (this.isSessionAuthenticated)
        {
            String allNotesJsonTxt = this.generateJsonString();
            File appDataFile = ToolUtils.getExpectedAppDataFile(this.appContext);

            //Encrypt the JSON Text
            String paddedEncKey = CryptoUtils.get_aes_key(newEncKey);
            String jsonTxtEnc = CryptoUtils.encryptString(paddedEncKey, allNotesJsonTxt);

            byte[] bytesText = jsonTxtEnc.getBytes();
            ToolUtils.writeDataIntoFile(bytesText, appDataFile);
            SessionManager.updateAppSession(this.appContext, paddedEncKey);
            successStatus = true;
        }

        return successStatus;
    }

    protected Map<Integer, NoteDetails> fetchAllNoteDetails()
    {
        if (this.isSessionAuthenticated)
        {
            byte[] allNoteBytes = ToolUtils.readAppJsonDataBytes(this.appContext);
            String encryptedAllNotes = new String(allNoteBytes);

            // Decrypting the Notes
            String encryptionKey = SessionManager.getAppDataEncryptionKey(this.appContext);
            String decryptedAllNotes = CryptoUtils.decryptString(encryptionKey, encryptedAllNotes);

            //Convert decrypted text to JSON Object
            JSONObject rootAllNotes = ToolUtils.convertJsonTextToJsonObject(decryptedAllNotes, "All Notes");
            Iterator<String> keys = rootAllNotes.keys();

            this.initializeAllNotes();
            try {
                while (keys.hasNext()) {
                    String key = keys.next();
                    int key_int = Integer.parseInt(key);

                    JSONObject thisNoteDetailsJson = (JSONObject) rootAllNotes.get(key);
                    NoteDetails thisNoteDetails = new NoteDetails().getNoteDetailsFromJsonObject(thisNoteDetailsJson);

                    allNoteDetails.put(key_int, thisNoteDetails);
                }
            } catch (Exception exp) {
                // Exception Occurred
            }
        }

        return allNoteDetails;
    }

    protected Map<Integer, NoteDetails> getAllNoteDetails()
    {
        if (this.isSessionAuthenticated)
        {
            return allNoteDetails;
        }
        return new TreeMap<>();
    }

    protected void removeAllNotes()
    {
        if (isSessionAuthenticated)
        {
            allNoteDetails = null;
        }
    }

}
