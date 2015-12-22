/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileChangedMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;

/**
 *
 * @author Svetoslav
 */
public class FileChangedTask extends ARequestTask<FileChangedMessage> {
	@Override
	public void run() {
		this.doWork((PieFile)this.msg.getPieFolder());
	}
}
