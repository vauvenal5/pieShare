/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.logoutTask.api;

import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.*;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;

/**
 *
 * @author Richard
 */
public interface ILogoutFinished extends IPieCallable {

	void finished();
	
}
