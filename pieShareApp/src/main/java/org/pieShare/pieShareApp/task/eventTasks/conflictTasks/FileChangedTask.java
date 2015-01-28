/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.api.IFileChangedMessage;

/**
 *
 * @author Svetoslav
 */
public class FileChangedTask extends ARequestTask<IFileChangedMessage> {
	@Override
	public void run() {
		this.doWork(this.msg.getPieFile());
	}
}
