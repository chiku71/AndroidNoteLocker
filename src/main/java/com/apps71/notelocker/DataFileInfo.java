package com.apps71.notelocker;


class DataFileInfo
{
    // -------------------------------------------------------------------------------------------------------------------------------------------------
    // Encryption Key to Decrypt User Credentials. Don't change it.
    final private static String USER_DATA_ENCRYPTION_KEY = null;
    final private static String BACKUP_DATA_ENCRYPTION_KEY = null;
    // --------------------------------------------------------------------------------------------------------------------------------------------------

    // Stores All Notes created by User.
    final private static String APP_DATA_FILE = "ApplicationData.mnl";
    // Stores User Credentials.
    final private static String USER_DATA_FILE = "ToolData.mnl";
    // Stores Tool Settings as configured by User.
    final private static String APP_SETTINGS_CONFIG_FILE = "AppSettingsConfig.mnl";

    // Tool Welcome Note
    final private static  String WELCOME_NOTE_FILE = "Welcome_Note.txt";

    final protected static String WRITE_YOUR_OWN_SECURITY_QUESTION_TXT = "Have your own security question.";
    
    final private static String[] SECRET_QUESTIONS_SPINNER_OPTIONS = {
                                                    "Select your security question ...",
                                                    "What is your favorite movie ?",
                                                    "What was the name of the hospital where you were born ?",
                                                    "What is your pet's name ?",
                                                    "What is your mother's maiden name ?",
                                                    "What is your best friend's last name ?",
                                                    "Where was your father born ?",
                                                    WRITE_YOUR_OWN_SECURITY_QUESTION_TXT
    };

    protected static String getAppDataFileName() { return APP_DATA_FILE; }

    protected static  String getWelcomeNoteFileName() { return WELCOME_NOTE_FILE; }

    protected static String getUserDataFileName() { return USER_DATA_FILE; }

    protected static String getAppSettingsConfigFileName() { return APP_SETTINGS_CONFIG_FILE; }

    protected  static  String getBackUpFileEncKey()
    {
        return BACKUP_DATA_ENCRYPTION_KEY;
    }

    protected static String getUserDataEncryptionKey()
    {
        return USER_DATA_ENCRYPTION_KEY;
    }

    protected static String[] getSecretQuestions() { return SECRET_QUESTIONS_SPINNER_OPTIONS; }

}
