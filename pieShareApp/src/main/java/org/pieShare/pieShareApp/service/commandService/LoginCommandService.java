/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.commandService;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Svetoslav
 */
public class LoginCommandService implements ICommandService<LoginCommand> {

    IPasswordEncryptionService passwordEncryptionService;
    IClusterManagementService clusterManagementService;
    
    public void setPasswordEncryptionService(IPasswordEncryptionService service) {
        this.passwordEncryptionService = service;
    }
    
    @Override
    public void executeCommand(LoginCommand command) {
        EncryptedPassword pwd = this.passwordEncryptionService.encryptPassword(command.getPlainTextPassword());
        
        try {
            clusterManagementService.connect(command.getUserName());
        } catch (ClusterManagmentServiceException ex) {
            ex.printStackTrace();
            //todo-sv: ex handling
        }
    }
    
}
