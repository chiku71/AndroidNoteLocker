package com.apps71.notelocker;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager
{
    private static SharedPreferences sharedPref = null;

    public static SharedPreferences getSharedPref(Context appContext)
    {
        if (SessionManager.sharedPref == null)
        {
            return appContext.getSharedPreferences(Constants.APP_SESSION_NAME, Context.MODE_PRIVATE);
        }
        else
        {
            return SessionManager.sharedPref;
        }
    }

    public static void createAppSession(Context appContext, String encryptionKey)
    {
        SessionManager.sharedPref = getSharedPref(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.APP_DATA_ENCRYPTION_KEY, encryptionKey);
        editor.apply();
        editor.commit();
    }

    public static void updateAppSession(Context appContext, String encryptionKey)
    {
        createAppSession(appContext, encryptionKey);
    }

    public static String getAppDataEncryptionKey(Context appContext)
    {
        SharedPreferences sharedPref = getSharedPref(appContext);
        return sharedPref.getString(Constants.APP_DATA_ENCRYPTION_KEY, null);
    }

    public static void endAppSession(Context appContext)
    {
        SharedPreferences sharedPref = getSharedPref(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.APP_DATA_ENCRYPTION_KEY, null);
        //editor.clear();
        editor.apply();
        editor.commit();
    }

    protected static boolean authenticateSession(Context appContext)
    {
        boolean isSessionAuthentic = false;

        String encKeyFromSession = getAppDataEncryptionKey(appContext);

        // The AES Encryption Key must not be null and should have 16 characters.
        if (encKeyFromSession != null && encKeyFromSession.length() == 16)
        {
            // Getting current user data.
            UserDataJson userData = new UserDataJson(appContext);
            String userPassword = userData.getPassword();
            String paddedEncKey = CryptoUtils.get_aes_key(userPassword);

            if (paddedEncKey.equals(encKeyFromSession))
            {
                isSessionAuthentic = true;
            }
        }

        return isSessionAuthentic;
    }
}
