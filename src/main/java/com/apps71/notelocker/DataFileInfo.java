package com.apps71.notelocker;


class DataFileInfo
{
    // Stores All Notes created by User.
    final private static String APP_DATA_FILE = "ApplicationData.mnl";
    // Stores User Credentials.
    final private static String USER_DATA_FILE = "ToolData.mnl";
    // Stores Tool Settings as configured by User.
    final private static String APP_SETTINGS_CONFIG_FILE = "AppSettingsConfig.mnl";

    // Tool Welcome Note
    final private static  String WELCOME_NOTE_FILE = "Welcome_Note.txt";
    // Encryption Key to Decrypt User Credentials. Don't change it.
    final private static String USER_DATA_ENCRYPTION_KEY = null;
    final private static String BACKUP_DATA_ENCRYPTION_KEY = null;

    final private static String[] SECRET_QUESTIONS_SPINNER_OPTIONS = {
                                                    "Select your secret question ...",
                                                    "What is your favorite movie ?",
                                                    "What is the last name of the teacher who gave you your first failing grade ?",
                                                    "What was the name of the hospital where you were born ?",
                                                    "What is your pet's name ?",
                                                    "What is your mother's maiden name ?",
                                                    "What is your best friend's last name ?",
                                                    "Where was your father born ?",
                                                    "What is the middle name of your oldest child ?"
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
