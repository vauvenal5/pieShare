/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.commandService.api;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.LoginFinished;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Richard
 */
public interface ILogoutCommandService extends ICommandService<LoginCommand> {


}
