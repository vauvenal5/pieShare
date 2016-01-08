/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.resetPwd;

import java.io.IOException;
import java.nio.file.Files;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.ResetPwdCommand;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.loginService.UserTools;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.api.IResetPwdTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ResetPwdTask implements IResetPwdTask {

    private IDatabaseService databaseService;
    private ResetPwdCommand command;
    private IUserService userService;

    private UserTools userTools;

    public void setUserTools(UserTools userTools) {
        this.userTools = userTools;
    }

    public void setDatabaseService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void setEvent(ResetPwdCommand msg) {
        this.command = msg;
    }

    @Override
    public void run() {
        try {
            userTools.resetPassword();
        } catch (Exception ex) {
            PieLogger.error(this.getClass(), String.format("Error in Reset Password Task. Message: %s", ex.getMessage()));
        }
        
        command.getCallback().pwdResetOK();
        
    }
}
