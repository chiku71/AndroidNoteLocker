package com.apps71.notelocker;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils 
{
	final private static String ENCRYPTION_TYPE = "AES";
	final private static int ENCRYPT_MODE = Cipher.ENCRYPT_MODE;
	final private static int DECRYPT_MODE = Cipher.DECRYPT_MODE;

    public static String encryptString(String key, String textToEncrypt)
    {
        byte[] encryptedTextBytes;
        try
        {
            Cipher cipher = getCipherObject(key, ENCRYPT_MODE);
            encryptedTextBytes = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);
        }
        catch(Exception exp)
        {
            // Exception Occurred
        }
        return null;
    }


    public static String decryptString(String key, String textToDecrypt)
    {
        byte[] decryptedTextBytes;
        try
        {
            Cipher cipher = getCipherObject(key, DECRYPT_MODE);
            decryptedTextBytes = cipher.doFinal(Base64.decode(textToDecrypt, Base64.DEFAULT));
            return new String(decryptedTextBytes, "UTF-8");
        }
        catch (Exception exp)
        {
            // Exception occurred while Decrypting
        }
        return null;
    }

    private static Cipher getCipherObject(String key, int cipherMode)
    {
        Cipher cipher;
        try
        {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), ENCRYPTION_TYPE);
            cipher = Cipher.getInstance(ENCRYPTION_TYPE);
            cipher.init(cipherMode, secretKey);
            return cipher;
        }
        catch (Exception exp)
        {
            // Exception Occurred
        }
        return null;
    }

    
    public static String get_aes_key(String key)
    {
    	String aes_key = key;
    	if (key.length() > 16) 
    	{
    		aes_key = key.substring(0, 16);
    	}
    	else if (key.length() < 16)
    	{
    		aes_key = pad_alphabets_to_end(key, 16);
    	}
    	
    	return aes_key;
    }
    
    private static String pad_alphabets_to_end(String str, int desired_length)
    {
    	int num_1 = Constants.ADDITIONAL_CHAR_FOR_ENCRYPTION_KEY_FIRST;
        int num_2 = Constants.ADDITIONAL_CHAR_FOR_ENCRYPTION_KEY_SECOND;
        int num_3 = Constants.ADDITIONAL_CHAR_FOR_ENCRYPTION_KEY_THIRD;

        for (int i=1; i<=desired_length; i++)
    	{
            String new_char;
    	    if ((i % 3) == 0)
            {
                new_char = Character.toString((char)num_3);
            }
            else if ((i % 2) ==0)
            {
                new_char = Character.toString((char)num_2);
            }
            else
            {
                new_char = Character.toString((char)num_1);
            }

    		str = str + new_char;
    		num_1++;
    		num_2--;
    		num_3++;

    		if (str.length() == desired_length)
            {
                break;
            }
    	}

    	return str;
    }

}
