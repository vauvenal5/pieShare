/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 *
 * @author Svetoslav
 */
public class FileTransferMetaMessage extends HeaderMessage {
    private String filename;
    private String relativePath;
    private byte[] metaInfo;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public byte[] getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(byte[] metaInfo) {
        this.metaInfo = metaInfo;
    }
}
