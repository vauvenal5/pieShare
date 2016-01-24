/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.factoryService;

import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileChangedMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileDeletedMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileMovedMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileRenamedMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileCreatedMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderMovedMessage;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderRenamedMessage;
import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.metaMessage.MetaCommitMessage;


/**
 *
 * @author Svetoslav
 */
public interface IMessageFactoryService {
	FileChangedMessage getFileChangedMessage();
	
	FileDeletedMessage getFileDeletedMessage();
	
	IFileListMessage getFileListMessage();
	
	FileListRequestMessage getFileListRequestMessage();
	
	FileRequestMessage getFileRequestMessage();
	
	FileTransferCompleteMessage getFileTransferCompleteMessage();
	
	IMetaMessage getFileTransferMetaMessage();
	
	FileCreatedMessage getNewFileMessage();
	
	MetaCommitMessage getMetaCommitMessage();
        
        FileRenamedMessage getFileRenamedMessage();
        
        FileMovedMessage getFileMovedMessage();
        //Folder
        FolderCreateMessage getNewFolderMessage();
        
        FolderDeleteMessage getFolderDeletedMessage();

        FolderMovedMessage getFolderMovedMessage();

        FolderRenamedMessage getFolderRenamedMessage();  
}
