package com.apps71.notelocker;

import android.content.Context;
import android.os.Environment;
import android.text.Html;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUpDataToLocalDrive
{
    private Context appContext;
    private TextView outputMessage;

    public BackUpDataToLocalDrive(Context appContext, TextView outputMessage)
    {
        this.appContext = appContext;
        this.outputMessage = outputMessage;
    }

    protected boolean runBackUp()
    {
        FileAccessHelper fileAccessHelper = new FileAccessHelper(this.appContext);
        if (fileAccessHelper.isStorageAccessGranted())
        {
            // Create a New BackUp File
            File backUpFile = this.createNewBackUpFile();

            // Store current data into the backUp File.
            boolean outcome = this.writeDataIntoBackupFile(backUpFile);

            if (outcome)
            {
                String msg = "Data BackUp has been completed successfully. BackUp File path ";
                String filePath = "<br/><font color='#0C1481'>" + backUpFile + "</font>";
                String otherMsg = "<br/><br/><font color='#0C1481'>(</font>" +
                        "<font color='#F44336'>*</font>" +
                        "<font color='#000000'>Please remember your current Password.\nIt " +
                        "will be required during restoring your BackUp file.</font>" +
                        "<font color='#0C1481'>)</font>";
                this.outputMessage.setText(Html.fromHtml(msg + ":" + filePath + "'." + otherMsg));
            }
            else
            {
                String failedBackUpMsg = "Some issue occurred while processing BackUp.\nPlease try again later";
                ToastUtils.showFailureMessage(this.appContext, failedBackUpMsg);
            }
        }
        else
        {
            fileAccessHelper.showNoStoragePermissionError();
        }
        return false;
    }

    private boolean writeDataIntoBackupFile(File backUpFile)
    {
        boolean isOutcomeSuccessful = false;
        try
        {
            // Fetch the UserInfo and App Data
            byte[] userInfoDataBytes = ToolUtils.readUserJsonDataBytes(this.appContext);
            byte[] appInfoDataBytes = ToolUtils.readAppJsonDataBytes(this.appContext);

            String userInfoData = new String(userInfoDataBytes, "UTF-8");
            String appInfoData = new String(appInfoDataBytes, "UTF-8");

            // Prepare the BackUp data in JSON Format
            String backUpData = "{\"" +
                    Constants.BACKUP_USER_INFO + "\":\"" + userInfoData + "\", \"" +
                    Constants.BACKUP_APP_INFO +"\":\"" + appInfoData +"\"}";

            // Encrypt the backUp data
            String backUpEncKey = ToolUtils.getBackupInfoEncryptionKey();
            String encryptedBackUpData = CryptoUtils.encryptString(backUpEncKey, backUpData);

            // Write the encrypted BackUp data into the backup file.
            byte[] bytesText = encryptedBackUpData.getBytes();
            ToolUtils.writeDataIntoFile(bytesText, backUpFile);

            isOutcomeSuccessful = true;
        }
        catch (Exception ex)
        {
            // Exception Occurred.
        }

        return isOutcomeSuccessful;
    }

    private File createNewBackUpFile()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String backUpFolderName = Constants.BACKUP_FOLDER_NAME;
        String backUpFileName = Constants.BACKUP_FILE_NAME + timeStamp + Constants.BACKUP_FILE_FORMAT;

        // We already have permission for internal storage
        File backupFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator  + backUpFolderName);
        File backUpFile = new File(Environment.getExternalStorageDirectory() +
                File.separator  + backUpFolderName + File.separator + backUpFileName);

        // Create backup Folder
        backupFolder.mkdirs();
        ToolUtils.createFileWithStatus(backUpFile, "BackUp");

        return backUpFile;
    }
}
