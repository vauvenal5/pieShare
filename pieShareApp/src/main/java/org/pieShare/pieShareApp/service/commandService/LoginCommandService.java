/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.commandService;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.api.ILoginCommandService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.LoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;

/**
 *
 * @author Svetoslav
 */
public class LoginCommandService implements ILoginCommandService {

	private IBeanService beanService;
	private ILoginTask loginService;
	private PieExecutorService executorService;

	public void setExecuterService(PieExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setLoginService(ILoginTask loginService) {
		this.loginService = loginService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void executeCommand(LoginCommand command) {
		//loginService.setLoginCommand(command);
		//executorService.execute(loginService);
	}
}
