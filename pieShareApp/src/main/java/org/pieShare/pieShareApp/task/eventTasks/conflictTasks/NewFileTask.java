/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.api.IFileCreatedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;

/**
 *
 * @author Svetoslav
 */
public class NewFileTask extends ARequestTask<IFileCreatedMessage> {
	@Override
	public void run() {
		this.doWork((PieFile)this.msg.getPieFolder());
	}
}
