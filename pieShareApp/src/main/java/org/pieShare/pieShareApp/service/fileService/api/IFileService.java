/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author richy
 */
public interface IFileService {

	//public void remoteFileChange(FileChangedMessage message);

	//public boolean checkMergeFile(PieFile pieFile);
	
	public List<PieFile> getAllFiles() throws IOException;
	
//	public void remoteAllFilesSyncRequest(AllFilesSyncMessage msg);
//	public void sendAllFilesSyncRequest();

	public void deleteRecursive(PieFile file);
	
	public void waitUntilCopyFinished(String filePath);
}
