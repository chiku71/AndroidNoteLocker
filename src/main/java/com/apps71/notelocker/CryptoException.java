package com.apps71.notelocker;

public class CryptoException extends Exception 
{
	 
    public CryptoException() 
    {
        super("Issue occured while Encrypting/Decrypting the files.");
    }
 
    public CryptoException(String msg, Throwable thrw)
    {
        super(msg, thrw);
    }
}
