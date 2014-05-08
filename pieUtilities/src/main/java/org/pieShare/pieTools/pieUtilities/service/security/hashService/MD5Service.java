/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.hashService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author richy
 */
public class MD5Service
{
    private static final PieLogger  md5Logger =  new PieLogger(MD5Service.class);
    private MessageDigest messageDigest = null;
    
    private IProviderService provider;
    
    public MD5Service() {
        try
        {
            messageDigest = MessageDigest.getInstance(this.provider.getFileHashAlorithm());
        }
        catch (NoSuchAlgorithmException ex)
        {
            md5Logger.error("Error in MD5 Hash Algorithm, this shold no happen. Message: " + ex.getMessage());
        }
    }

    public byte[] hash(byte[] data) throws IOException
    {
        if(this.messageDigest == null)
        {
            //todo-sv: throw exception
        }

	messageDigest.update(data);
	byte[] resultByte = messageDigest.digest();
        this.messageDigest.reset();

        return resultByte;
    }
}
