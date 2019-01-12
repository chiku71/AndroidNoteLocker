package com.apps71.notelocker;

import org.json.JSONObject;

class JsonDataInfo
{
    private JSONObject jsonObj;
    private String encryptedText;

    public  JsonDataInfo(JSONObject jsonObj, String encryptedText)
    {
        this.jsonObj = jsonObj;
        this.encryptedText = encryptedText;
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public JSONObject getJsonObj() {
        return jsonObj;

    }
}
