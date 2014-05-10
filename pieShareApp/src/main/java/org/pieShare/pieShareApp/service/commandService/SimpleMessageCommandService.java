/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.commandService;

import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;

import org.pieShare.pieShareApp.model.message.SimpleMessage;
import org.pieShare.pieShareApp.model.command.SimpleMessageCommand;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageCommandService implements ICommandService<SimpleMessageCommand> {
    
    private IClusterService clusterService;

    @Override
    public void executeCommand(SimpleMessageCommand command) {
        try {
            SimpleMessage msg = new SimpleMessage();
            msg.setMsg(command.getMsg());
            
            this.clusterService.sendMessage(msg);
        } catch (ClusterServiceException ex) {
            //todo-sv: error handling
            ex.printStackTrace();
        }
    }
}
