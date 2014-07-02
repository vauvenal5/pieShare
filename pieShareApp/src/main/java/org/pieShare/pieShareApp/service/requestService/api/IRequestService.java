/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.requestService.api;

import java.util.ArrayList;
import java.util.HashMap;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Richard
 */
public interface IRequestService {
	public void requestFile(PieFile pieFile);
	public void anncounceRecived(FileTransferMetaMessage message);
	public HashMap<PieFile, Boolean> getRequestedFileList();
	public boolean deleteRequestedFile(PieFile pieFile);
}
