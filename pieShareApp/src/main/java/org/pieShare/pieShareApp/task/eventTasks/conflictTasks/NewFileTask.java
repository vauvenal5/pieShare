/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.api.INewFileMessage;

/**
 *
 * @author Svetoslav
 */
public class NewFileTask extends ARequestTask<INewFileMessage> {
	@Override
	public void run() {
		this.doWork(this.msg.getPieFile());
	}
}
