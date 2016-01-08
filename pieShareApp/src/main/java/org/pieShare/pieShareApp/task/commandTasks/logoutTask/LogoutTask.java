/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.logoutTask;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.service.loginService.UserTools;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;

/**
 *
 * @author Richard
 */
public class LogoutTask implements ILogoutTask {

    private IClusterManagementService clusterManagementService;
    private LogoutCommand logoutCommand;
    private IUserService userService;

    private UserTools userTools;

    public void setUserTools(UserTools userTools) {
        this.userTools = userTools;
    }

    public void setClusterManagementService(IClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private void logout() {
        userTools.logout();
    }

    @Override
    public void run() {
        logout();
        if (logoutCommand.getCallback() != null) {
            logoutCommand.getCallback().finished();
        }
    }

    @Override
    public void setEvent(LogoutCommand msg) {
        this.logoutCommand = msg;
    }
}
