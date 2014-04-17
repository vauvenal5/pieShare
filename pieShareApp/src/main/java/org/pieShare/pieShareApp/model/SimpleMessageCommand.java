/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model;

import org.pieShare.pieShareApp.service.commandService.ICommand;
import org.pieShare.pieTools.pieCeption.model.action.ICommandMessage;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageCommand extends HeaderMessage implements ICommandMessage, ICommand {
    
    private String msg;
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public SimpleMessage getSimpleMessage() {
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg(this.msg);
        return msg;
    }
    
    @Override
    public void executeCommand() {
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg(this.msg);
        //send msg to cluster
    }
}
