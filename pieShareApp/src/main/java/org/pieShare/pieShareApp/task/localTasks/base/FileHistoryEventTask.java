/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks.base;

import org.pieShare.pieShareApp.model.message.base.FileHistoryMessageBase;

/**
 *
 * @author Svetoslav
 */
public abstract class FileHistoryEventTask extends FileEventTask {
	
	protected void doWork(FileHistoryMessageBase msg) {
		//todo: add insert of history here
		
		super.doWork(msg);
	}
}
