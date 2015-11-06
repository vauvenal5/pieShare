/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public class FileListTask extends ARequestTask<IFileListMessage>  {
	@Override
	public void run() {
		for(PieFile file: this.msg.getFileList()) {
			//in future start an own task for requesting files because we don't 
			//want to block by user resolution all autoresolvable requests
			
			this.doWork(file);
		}
	}
}
