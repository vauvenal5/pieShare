/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

import java.io.File;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IShareService {

	//void shareFile(PieFile file);

	//void handleFile(PieFile file, byte[] metaInfo);
	
	File prepareFile(PieFile file) throws NoLocalFileException;
	
	File handleFile(PieFile file)  throws AllreadyInitializedException;
	
	void localFileTransferComplete(PieFile file, boolean source);
	
	void remoteFileTransferComplete(PieFile file);
	
	void handleRemoteRequestForActiveShare(PieFile pieFile);
	
	boolean isShareActive(PieFile pieFile);
	
	boolean isPrepared(PieFile file);
	
	/**
	 * Returns false if file already initialized
	 * Returns true if file was successfully initialized
	 * @param file
	 * @param count
	 * @return 
	 */
	//boolean initCheckPieFileState(PieFile file);
	
	/**
	 * Returns false if file already initialized
	 * Returns true if file was successfully initialized
	 * @param file
	 * @param count
	 * @return 
	 */
	//boolean initCheckPieFileState(PieFile file, Integer count);
}
