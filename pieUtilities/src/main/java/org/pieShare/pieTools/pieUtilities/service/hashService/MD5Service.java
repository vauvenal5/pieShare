/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.hashService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class MD5Service {

    public static String MD5(File file) throws IOException
    {
        PieLogger logger = new PieLogger(MD5Service.class);

        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) 
        {
            logger.error("Error in MD5 Hash Algorithm, this shold no happen. Message: " + ex.getMessage());
            return null;
        }
        messageDigest.reset();

        byte[] data = Files.readAllBytes(file.toPath());

        messageDigest.update(data);

        final byte[] resultByte = messageDigest.digest();

        return new String(resultByte);

    }
}
