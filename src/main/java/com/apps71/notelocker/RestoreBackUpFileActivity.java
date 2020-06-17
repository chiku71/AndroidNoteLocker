package com.apps71.notelocker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class RestoreBackUpFileActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * This activity is to restore the backup file taken prior in the previous installation.
     *      --> Select the BackUp file using system's file picker
     *      --> Authenticate user's identity via the old credentials stored in the backup file.
     *      --> Create the ApplicationData and UserData file using the data from the BackUP file.
     *      --> Take user to the Home Page.
     */

    // Declaring Class variables
    private static final int BACKUP_FILE_PICKER_CODE = 0;

    Button chooseBackUpFileBtn;
    ImageButton proceedToRestoreBackUpBtn;
    TextView selectedFileName;
    EditText passwordProvidedByUser;

    File selectedBckpFile = null;
    String className = ToolUtils.getClassNameFromObject(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_back_up_file);

        // SettingUp UI elements and action listeners
        this.initializeUIVariables();
        this.setUpActionListeners();

        //Adding the back button to the App Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpActionListeners()
    {
        this.chooseBackUpFileBtn.setOnClickListener(this);
        this.proceedToRestoreBackUpBtn.setOnClickListener(this);
    }

    private void initializeUIVariables()
    {
        this.chooseBackUpFileBtn = findViewById(R.id.select_backup_file_new_installation);
        this.proceedToRestoreBackUpBtn = findViewById(R.id.import_backup_file_new_installation);

        this.selectedFileName = findViewById(R.id.selected_backup_file_to_restore_text_view_name);
        this.passwordProvidedByUser = findViewById(R.id.previous_password_in_backup_file);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.select_backup_file_new_installation:
                // Select the backup file using File picker
                this.browseAndSelectTheBackUpFile();
                break;

            case R.id.import_backup_file_new_installation:
                // Process the user backup file for first use.
                this.processSelectedBackUpFile();
                break;
        }
    }

    private void browseAndSelectTheBackUpFile()
    {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        this.showFilePicker();
    }

    private void showFilePicker()
    {
        Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filePickerIntent.setType("*/*");
        filePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);

        try
        {
            startActivityForResult(Intent.createChooser(filePickerIntent,"Select the BackUp file"), BACKUP_FILE_PICKER_CODE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            String msg = "No File manager installed in your device.";
            ToastUtils.showFailureMessage(this, msg + "\nPlease install a File Manager from PlayStore.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case BACKUP_FILE_PICKER_CODE:
                String selectedFile = null;
                if (resultCode == RESULT_OK && data != null)
                {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Context appContext = getApplicationContext();
                    FilePickerHelper filePickerHelper = new FilePickerHelper(uri, appContext);
                    selectedFile = filePickerHelper.getFilePath();

                    if (selectedFile == null || !this.isSelectedFileValid(selectedFile))
                    {
                        // Show error
                        this.showInvalidSelectedFileError();
                    }
                    else
                    {
                        this.selectedBckpFile = new File(selectedFile);
                        String[] splittedFileName = selectedFile.split("/");
                        String fileName = splittedFileName[splittedFileName.length - 1];
                        this.selectedFileName.setText(fileName);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isSelectedFileValid(String filePath)
    {
        File file = new File(filePath);
        if ((filePath.length() > 7 && filePath.substring(filePath.length() - 7).equals(".nlbckp")) && file.isFile())
        {
            return true;
        }
        return false;
    }

    private void showInvalidSelectedFileError()
    {
        ToastUtils.showFailureMessage(this, "Please select a valid NoteLocker BackUp file.");
    }

    private void processSelectedBackUpFile()
    {
        // Method to process the selected back Up file.
        if (this.selectedBckpFile != null)
        {
            FileAccessHelper fileAccessHelper = new FileAccessHelper(this);
            if (fileAccessHelper.isStorageAccessGranted())
            {
                boolean allDataFound = false;
                boolean authenticatedSuccessfully = false;
                try
                {
                    // Get the decrypted Info from the Selected BackUp file.
                    JsonDataInfo bckpJsonInfo = this.getDecryptedBackUpData();
                    JSONObject bckpJson = bckpJsonInfo.getJsonObj();

                    // Get the decrypted User Info from the Selected BackUp file.
                    JsonDataInfo usrDataJsonInfo =this.getDecryptedData(bckpJson, null);
                    JSONObject userInfoJson = usrDataJsonInfo.getJsonObj().getJSONObject(Constants.USER_DATA_JSON);

                    JsonDataInfo appDataJsonInfo = null;
                    String userPassword = null;

                    // Authenticate if the Provided password matches with the User password in the Back Up file.
                    if (this.isCorrectPasswordProvided(userInfoJson))
                    {
                        authenticatedSuccessfully = true;
                        // Get the decrypted App Info from the Selected BackUp file.
                        userPassword = userInfoJson.getString(Constants.PASSWORD_JSON);
                        appDataJsonInfo =this.getDecryptedData(bckpJson, userPassword);

                        // All data found in the Selected BackUp File
                        allDataFound = true;
                    }

                    if (allDataFound)
                    {
                        boolean isBackUpSuccessful = this.restoreBackUpData(usrDataJsonInfo.getEncryptedText(), appDataJsonInfo.getEncryptedText());
                        if (isBackUpSuccessful)
                        {
                            Intent userHomeActivity = new Intent(RestoreBackUpFileActivity.this, UserHome.class);
                            userHomeActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
                            userHomeActivity.putExtra(Constants.USER_PASSWORD, userPassword);
                            userHomeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SessionManager.createAppSession(getApplicationContext(), CryptoUtils.get_aes_key(userPassword));
                            startActivity(userHomeActivity);
                        }
                        else
                        {
                            throw new Exception("Back up is unsuccessful.");
                        }
                    }
                    else if(authenticatedSuccessfully)
                    {
                        throw new Exception("Unable to fetch all data from selected BackUp File.");
                    }
                }
                catch (Exception ex)
                {
                    String msg = "Some issue occurred while processing the selected backup file.";
                    ToastUtils.showFailureMessage(this, msg  + "\nPlease try again.");
                }
            }
            else
            {
                fileAccessHelper.showNoStoragePermissionError();
            }
        }
        else
        {
            this.showInvalidSelectedFileError();
        }
    }

    private boolean restoreBackUpData(String userDataEncText, String appDataEncText)
    {
        File[] signUpDataFiles = ToolUtils.createSignUpDataFiles(this);
        File newUserDataFile = signUpDataFiles[0];
        File newAppDataFile = signUpDataFiles[1];
        File defaultSettingsConfigFile = signUpDataFiles[2];
        // New Tool Files has been created ....

        if (newUserDataFile != null && newAppDataFile != null && defaultSettingsConfigFile != null)
        {
            // Writing data from BackUp file
            this.writeDataToFile(userDataEncText, newUserDataFile);
            this.writeDataToFile(appDataEncText, newAppDataFile);

            // Preparing new Settings Config file
            ToolSettingConfig defaultSettings = new ToolSettingConfig(getApplicationContext());
            defaultSettings.storeData();

            return true;
        }
        return false;
    }

    private void writeDataToFile(String encryptedUserData, File file)
    {
        byte[] bytesText = encryptedUserData.getBytes();
        ToolUtils.writeDataIntoFile(bytesText, file);
    }

    private boolean isCorrectPasswordProvided(JSONObject userInfoJson) throws JSONException
    {
        String enteredUserPassword = this.passwordProvidedByUser.getText().toString();

        if (enteredUserPassword != null && ! enteredUserPassword.trim().equals(""))
        {
            if (enteredUserPassword.equals(userInfoJson.getString(Constants.PASSWORD_JSON)))
            {
                return true;
            }
            else
            {
                String msg = "Incorrect password.\nPlease try again ...";
                ToastUtils.showFailureMessage(this, msg);
            }
        }
        else
        {
            String msg = "Please provide your old password to import the Back Up file.";
            ToastUtils.showFailureMessage(this, msg);
        }

        return false;
    }

    private JsonDataInfo getDecryptedBackUpData()
    {
        byte[] encryptedBckpFileData = ToolUtils.readFileData(this.selectedBckpFile);
        String encBckpFileTxt = new String(encryptedBckpFileData);

        // Decrypting back up File
        String backUpEncKey = ToolUtils.getBackupInfoEncryptionKey();
        String decryptedBckpFileTxt = CryptoUtils.decryptString(backUpEncKey, encBckpFileTxt);

        //Convert decrypted text to JSON Object
        JSONObject decBckpJson = ToolUtils.convertJsonTextToJsonObject(decryptedBckpFileTxt, "Back Up File");

        return new JsonDataInfo(decBckpJson, encBckpFileTxt);
    }

    private JsonDataInfo getDecryptedData(JSONObject decryptedBckpJson, String appDataEncKey) throws JSONException
    {
        String dataInfo = Constants.BACKUP_USER_INFO;
        String encKey = ToolUtils.getUserInfoEncryptionKey();
        if (appDataEncKey != null)
        {
            dataInfo = Constants.BACKUP_APP_INFO;
            encKey = CryptoUtils.get_aes_key(appDataEncKey);
        }

        String encInfoTxt = decryptedBckpJson.getString(dataInfo);
        String decInfoTxt = CryptoUtils.decryptString(encKey, encInfoTxt);

        //Convert decrypted text to JSON Object
        JSONObject infoJson = ToolUtils.convertJsonTextToJsonObject(decInfoTxt, "User/App Info Bckp File");

        return new JsonDataInfo(infoJson, encInfoTxt);
    }

    @Override
    public void onBackPressed()
    {
        Intent newInstallationActivity = new Intent(RestoreBackUpFileActivity.this, NewInstallationRunner.class);
        newInstallationActivity.putExtra(Constants.FROM_ACTIVITY, this.className);
        startActivity(newInstallationActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ToolUtils.closeApplication();
    }

}
