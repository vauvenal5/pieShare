/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.commandService;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.SimpleMessageCommand;
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
            this.clusterService.sendMessage(command.getSimpleMessage());
        } catch (ClusterServiceException ex) {
            Logger.getLogger(SimpleMessageCommandService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
