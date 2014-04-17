/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.actionService;

import java.util.HashMap;
import java.util.Map;
import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.SimpleMessageCommand;
import org.pieShare.pieTools.pieCeption.model.action.CommandAction;
import org.pieShare.pieTools.pieCeption.model.action.ICommandMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageActionService extends CommandAction {
    @Override
    public String getCommandName() {
        return "sendMsg";
    }

    @Override
    public String getProgramName() {
        return "pieShare";
    }

    @Override
    public Map<String, Class> getArguments() {
        Map<String, Class> args = new HashMap<>();
        args.put("msg", String.class);
        
        return args;
    }

    @Override
    public ICommandMessage getCommandMessage() {
        SimpleMessageCommand command = null;
        command.setMsg((String)this.args.get("msg"));
        return command;
    }
}
