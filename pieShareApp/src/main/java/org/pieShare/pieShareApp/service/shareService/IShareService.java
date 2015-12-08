/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

import java.io.File;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IShareService {
	
	File prepareFile(PieFile file) throws NoLocalFileException;
	
	void localFileTransferComplete(PieFile file, boolean source);
	
	void revokePrepared(PieFile file);
	
	boolean isPrepared(PieFile file);
}
