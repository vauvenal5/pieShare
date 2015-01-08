/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.api;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;

/**
 *
 * @author Svetoslav
 */
public interface IFileListMessage extends IEncryptedMessage {

	List<PieFile> getFileList();

	void setFileList(List<PieFile> list);
	
}
