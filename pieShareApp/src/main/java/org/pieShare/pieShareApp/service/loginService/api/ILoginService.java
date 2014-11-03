/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.loginService.api;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.loginService.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.service.loginService.event.LoginFinished;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Richard
 */
public interface ILoginService extends IPieTask {

	void setLoginCommand(LoginCommand command);
	IEventBase<ILoginFinishedListener, LoginFinished> getLoginFinishedEventBase();
}
