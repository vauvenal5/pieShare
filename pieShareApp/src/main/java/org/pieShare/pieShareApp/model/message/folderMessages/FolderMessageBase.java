/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.message.folderMessages;

import org.pieShare.pieShareApp.model.message.api.IFilderMessageBase;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieTools.piePlate.model.message.AClusterMessage;

/**
 *
 * @author daniela
 */
public class FolderMessageBase extends AClusterMessage implements IFilderMessageBase<PieFolder>{
    private PieFolder pieFolder;
    
    public PieFolder getPieFolder() {
        return pieFolder;
    }
    
    public void setPieFolder(PieFolder pieFolder) {
        this.pieFolder = pieFolder;
    }
}
