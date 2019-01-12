package com.apps71.notelocker;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;

class ToolSettingConfig extends JsonDataFIle
{
    /**
     * Stores the Tool Setting Config Data in a JSON file.
     * {
     *     "tool_settings" :
     *              {
     *                  "tool_time_out_mins" : 5
     *              }
     * }
     *
     * ## Default Time Out Period is 5 Minutes.
     */

    private Context appContext;
    private int toolTimeOutMins = 5; // Default 5 Mins


    public ToolSettingConfig(Context appContext)
    {
        this.appContext = appContext;
        this.populateSettingsDataFromConfigFile();
    }

    private void populateSettingsDataFromConfigFile()
    {
        if (ToolUtils.checkSettingsConfigFileExistence(this.appContext))
        {
            JSONObject settingConfigJson = this.getSettingsConfigData(this.appContext);
            try
            {
                this.toolTimeOutMins = settingConfigJson.getInt(Constants.TOOL_TIME_OUT_MINS_JSON);
            }
            catch (Exception ex)
            {
                // Some error occurred while parsing Settings Config Data file.
            }
        }
    }


    private JSONObject getSettingsConfigData(Context appContext)
    {
        JSONObject settingsConfigObj = null;
        try
        {
            //Read the data from file
            byte[] rawFileData = ToolUtils.readSettingsConfigDataBytes(appContext);
            String rawText = new String(rawFileData, "UTF-8");

            // Decrypting the UserData JSON Text
            String userInfoEncryptionKey = ToolUtils.getUserInfoEncryptionKey();
            String decryptedText = CryptoUtils.decryptString(userInfoEncryptionKey, rawText);

            settingsConfigObj = this.getSettingsConfigDataFromJsonText(decryptedText);
        }
        catch (Exception e)
        {
            // Exception Occurred
        }
        return settingsConfigObj;
    }

    private JSONObject getSettingsConfigDataFromJsonText(String jsonText)
    {
        JSONObject settingsConfigObj = null;
        try
        {
            // Get JSONObject from JSON data
            JSONObject jsonObj = ToolUtils.convertJsonTextToJsonObject(jsonText, "Settings Config");
            // Fetch JSONObject named user_data
            settingsConfigObj = jsonObj.getJSONObject(Constants.TOOL_SETTINGS_JSON);
        }
        catch (Exception e)
        {
            // Exception Occurred
        }
        return settingsConfigObj;
    }

    @Override
    protected String generateJsonString()
    {
        String jsonString = "{\n\"" + Constants.TOOL_SETTINGS_JSON + "\" : {\n";
        jsonString = jsonString + "\"" + Constants.TOOL_TIME_OUT_MINS_JSON + "\":" + this.toolTimeOutMins +"\n";

        jsonString = jsonString + "}\n}";
        return jsonString;
    }


    protected void storeData()
    {
        // Method to store the available User Data in the user data file.
        File toolSettingsConfigFile = this.getSettingsConfigFile();
        this.storeDataInToolSettingsConfigFile(toolSettingsConfigFile);
    }

    private void storeDataInToolSettingsConfigFile(File toolSettingsConfigFile)
    {
        String thisJsonData = this.generateJsonString();

        // Encrypting the UserData JSON text
        String encryptionKey = ToolUtils.getUserInfoEncryptionKey();
        String jsonTxtEnc = CryptoUtils.encryptString(encryptionKey, thisJsonData);

        byte[] bytesText = jsonTxtEnc.getBytes();
        ToolUtils.writeDataIntoFile(bytesText, toolSettingsConfigFile);
    }

    private File getSettingsConfigFile()
    {
        return ToolUtils.getExpectedSettingsConfigFile(this.appContext);
    }

    public int getToolTimeOutMins() {
        return toolTimeOutMins;
    }

    public void setToolTimeOutMins(int toolTimeOutMins) {
        this.toolTimeOutMins = toolTimeOutMins;
    }
}
