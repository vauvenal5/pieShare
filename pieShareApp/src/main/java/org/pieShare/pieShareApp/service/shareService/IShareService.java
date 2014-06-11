/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

import java.io.File;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IShareService {
    
    void shareFile(PieFile file);
    
    void handleFile(FileTransferMetaMessage msg);
    
}
