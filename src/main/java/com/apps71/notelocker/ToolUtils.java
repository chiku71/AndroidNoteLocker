package com.apps71.notelocker;
import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolUtils
{
    final private static String userInfoJsonFile = DataFileInfo.getUserDataFileName();
    final private static String applicationDataStoreFile = DataFileInfo.getAppDataFileName();
    final private static String toolSettingsConfigFile = DataFileInfo.getAppSettingsConfigFileName();
    final private static String userInfoEncryptionKey = CryptoUtils.get_aes_key(DataFileInfo.getUserDataEncryptionKey());
    final private static String backUpEncryptionKey = CryptoUtils.get_aes_key(DataFileInfo.getBackUpFileEncKey());


    protected static String getUserInfoEncryptionKey()
    {
        return userInfoEncryptionKey;
    }

    protected static String getBackupInfoEncryptionKey()
    {
        return backUpEncryptionKey;
    }

    public static void writeDataIntoFile(byte[] data, File file)
    {
        try
        {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(data);
            outStream.close();
        }
        catch (Exception exp)
        {
            // Unable to write data into file
        }
    }

    private static byte[] tryReadingFileBytes(File fileData) throws IOException
    {
        byte[] in_bytes;
        InputStream inStream = new FileInputStream(fileData);
        int size = inStream.available();
        in_bytes = new byte[size];
        inStream.read(in_bytes);
        inStream.close();

        return in_bytes;
    }


    public static byte[] readFileData(File dataFile)
    {
        byte[] in_bytes;

        try
        {
            in_bytes = tryReadingFileBytes(dataFile);
        }
        catch (IOException ex)
        {
            // Error in reading file
            return null;
        }

        return in_bytes;

    }

    protected static byte[] readJsonData(Context appContext, String fileName)
    {
        File dataFile = getExpectedDataFile(appContext, fileName);
        return readFileData(dataFile);
    }

    protected static byte[] readAppJsonDataBytes(Context appContext)
    {
        return readJsonData(appContext, applicationDataStoreFile);
    }

    protected static byte[] readUserJsonDataBytes(Context appContext)
    {
        return readJsonData(appContext, userInfoJsonFile);
    }

    protected static byte[] readSettingsConfigDataBytes(Context appContext)
    {
        return readJsonData(appContext, toolSettingsConfigFile);
    }


    public static JSONObject convertJsonTextToJsonObject(String jsonText, String jsonDesc)
    {
        JSONObject jsonObj = null;
        try
        {
            // Get JSONObject from JSON data
            jsonObj = new JSONObject(jsonText);
        }
        catch (Exception e)
        {
            // Error occurred while parsing JSON Text to Object for " + jsonDesc + "
        }

        return jsonObj;
    }

    public static String getClassNameFromObject(Object ob)
    {
        return ob.getClass().getSimpleName();
    }

    public static String getCurrentDateAndTime()
    {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        Date dateObj = new Date();
        return dateFormat.format(dateObj);
    }

    protected static void closeApplication()
    {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    private static boolean checkDataFileExistence(Context appContext, String fileName)
    {
        File f = getExpectedDataFile(appContext, fileName);
        if (f.isFile())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected static boolean checkUserDataFileExistence(Context appContext)
    {
        return checkDataFileExistence(appContext, userInfoJsonFile);
    }

    protected static boolean checkAppDataFileExistence(Context appContext)
    {
        return checkDataFileExistence(appContext, applicationDataStoreFile);
    }

    protected static boolean checkSettingsConfigFileExistence(Context appContext)
    {
        return checkDataFileExistence(appContext, toolSettingsConfigFile);
    }


    protected static File getExpectedToolDataFile(Context appContext)
    {
        return getExpectedDataFile(appContext, userInfoJsonFile);
    }

    protected static File getExpectedAppDataFile(Context appContext)
    {
        return getExpectedDataFile(appContext, applicationDataStoreFile);
    }

    protected static File getExpectedSettingsConfigFile(Context appContext)
    {
        return getExpectedDataFile(appContext, toolSettingsConfigFile);
    }

    private static File getExpectedDataFile(Context appContext, String expectedFileName)
    {
        String dataPathDir = appContext.getFilesDir().getAbsolutePath();
        String expectedFile = dataPathDir + "//" + expectedFileName;

        return new File(expectedFile);
    }

    protected static File[] getStoredDataFiles(Context appContext)
    {
        File[] storedFiles = new File[3];
        storedFiles[0] = getExpectedToolDataFile(appContext);
        storedFiles[1] = getExpectedAppDataFile(appContext);
        storedFiles[2] = getExpectedSettingsConfigFile(appContext);

        return storedFiles;
    }

    protected static File[] createSignUpDataFiles(Context appContext)
    {
        File[] createdFiles = new File[3];
        File newUserDataFile = getExpectedToolDataFile(appContext);
        File newAppDataFile = getExpectedAppDataFile(appContext);
        File defaultSettingsConfigFile = getExpectedSettingsConfigFile(appContext);

        createdFiles[0] = createFileWithStatus(newUserDataFile, "User Data");
        createdFiles[1] = createFileWithStatus(newAppDataFile, "Application Data");
        createdFiles[2] = createFileWithStatus(defaultSettingsConfigFile, "Settings Config");

        return createdFiles;
    }

    protected static File createFileWithStatus(File fileToCreate, String fileDesc)
    {
        try
        {
            boolean fileCreationStatus = fileToCreate.createNewFile();
            if (fileCreationStatus)
            {
                // A new " + fileDesc + " file has been created successfully");
            }
            else
            {
                // Failed to create a new " + fileDesc + " file.");
            }
            return fileToCreate;
        }
        catch (Exception ex)
        {
            // Exception Occurred
            return null;
        }
    }

    protected static String getTextFromAssetFile(Context appContext, String assetFileName)
    {
        try
        {
            InputStream inStream = appContext.getAssets().open(assetFileName);
            int size = inStream.available();
            byte[] inBytes = new byte[size];
            inStream.read(inBytes);
            inStream.close();

            return new String(inBytes);
        }
        catch (IOException ex)
        {
            // Error in reading file : "+ ex.getMessage());
        }
        return null;
    }

}
