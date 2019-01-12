package com.apps71.notelocker;


import android.content.Context;

import org.json.JSONObject;

import java.io.File;

class UserDataJson  extends  JsonDataFIle
{
    /**
     * Stores the User Data in a JSON file.
     * {
     *     "user_data" :
     *              {
     *                  "password" : "XXXX",
     *                  "security_question" : "XXXX",
     *                  "security_answer" : "XXXX"
     *              }
     * }
     */

    private String password;
    private String securityQuestion;
    private String securityAnswer;

    public UserDataJson(String _password, String _securityQuestion, String _securityAnswer)
    {
        this.password = _password;
        this.securityQuestion = _securityQuestion;
        this.securityAnswer = _securityAnswer;
    }

    public UserDataJson(Context appContext)
    {
        this._setUpUserDataFromAppContext(appContext);
    }

    private void _setUpUserDataFromAppContext(Context appContext)
    {
        if (ToolUtils.checkUserDataFileExistence(appContext))
        {
            JSONObject userData = this.getUserData(appContext);
            try
            {
                this.password = userData.getString(Constants.PASSWORD_JSON);
                this.securityQuestion = userData.getString(Constants.SECURITY_QUESTION_JSON);
                this.securityAnswer = userData.getString(Constants.SECURITY_ANSWER_JSON);
            }
            catch (Exception ex)
            {
                // Some error occurred while parsing User Data file
            }

        }
    }

    private JSONObject getUserData(Context appContext)
    {
        JSONObject usrObj = null;
        try
        {
            //Read the data from file
            byte[] rawFileData = ToolUtils.readUserJsonDataBytes(appContext);
            String rawText = new String(rawFileData, "UTF-8");

            // Decrypting the UserData JSON Text
            String userInfoEncryptionKey = ToolUtils.getUserInfoEncryptionKey();
            String decryptedText = CryptoUtils.decryptString(userInfoEncryptionKey, rawText);

            usrObj = this.getUserDataFromJsonText(decryptedText);
        }
        catch (Exception e)
        {
            // Exception Occurred
        }
        return usrObj;
    }

    private JSONObject getUserDataFromJsonText(String jsonText)
    {
        JSONObject usrObj = null;
        try
        {
            // Get JSONObject from JSON data
            JSONObject jsonObj = ToolUtils.convertJsonTextToJsonObject(jsonText, "User Details");
            // Fetch JSONObject named user_data
            usrObj = jsonObj.getJSONObject(Constants.USER_DATA_JSON);
        }
        catch (Exception e)
        {
            // Exception Occurred
        }
        return usrObj;
    }

    @Override
    protected String generateJsonString()
    {
        String jsonString = "{\n\"" + Constants.USER_DATA_JSON +"\" : {\n";
        jsonString = jsonString + "\"" + Constants.PASSWORD_JSON+ "\":\""+ this.password +"\",\n";
        jsonString = jsonString + "\"" + Constants.SECURITY_QUESTION_JSON + "\":\""+ this.securityQuestion +"\",\n";
        jsonString = jsonString + "\"" + Constants.SECURITY_ANSWER_JSON + "\":\""+ this.securityAnswer +"\"\n";

        jsonString = jsonString + "}\n}";
        return jsonString;
    }

    protected void storeData(Context appContext)
    {
        // Method to store the available User Data in the user data file.
        File userDataFile = ToolUtils.getExpectedToolDataFile(appContext);
        String thisJsonData = this.generateJsonString();

        String encryptionKey = ToolUtils.getUserInfoEncryptionKey();
        // Encrypting the UserData JSON text
        String jsonTxtEnc = CryptoUtils.encryptString(encryptionKey, thisJsonData);

        byte[] bytesText = jsonTxtEnc.getBytes();
        ToolUtils.writeDataIntoFile(bytesText, userDataFile);
    }

    // Getter and Setter methods.
    protected String getPassword() {
        return this.password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getSecurityQuestion() {
        return securityQuestion;
    }

    protected void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    protected String getSecurityAnswer() {
        return securityAnswer;
    }

    protected void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
