/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.factoryService;

import org.pieShare.pieShareApp.model.message.api.IFileChangedMessage;
import org.pieShare.pieShareApp.model.message.api.IFileDeletedMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.api.IFileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaCommitMessage;
import org.pieShare.pieShareApp.model.message.api.INewFileMessage;

/**
 *
 * @author Svetoslav
 */
public interface IMessageFactoryService {
	IFileChangedMessage getFileChangedMessage();
	
	IFileDeletedMessage getFileDeletedMessage();
	
	IFileListMessage getFileListMessage();
	
	IFileListRequestMessage getFileListRequestMessage();
	
	IFileRequestMessage getFileRequestMessage();
	
	IFileTransferCompleteMessage getFileTransferCompleteMessage();
	
	IFileTransferMetaMessage getFileTransferMetaMessage();
	
	INewFileMessage getNewFileMessage();
	
	IMetaCommitMessage getMetaCommitMessage();
}
