/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.model;

import javax.crypto.SecretKey;

/**
 *
 * @author Svetoslav
 */
public class EncryptedPassword {

    private byte[] password;
    private SecretKey secretKey;
    private byte[] iv;

    public void setPassword(byte[] pwd) {
        this.password = pwd;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
