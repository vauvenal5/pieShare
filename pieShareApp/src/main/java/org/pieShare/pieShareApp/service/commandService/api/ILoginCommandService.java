/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.commandService.api;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.loginService.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.service.loginService.event.LoginFinished;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Richard
 */
public interface ILoginCommandService extends ICommandService<LoginCommand> {

	IEventBase<ILoginFinishedListener, LoginFinished> getLoginServiceEventBase();

}
