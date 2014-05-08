/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.actionService;

import java.util.HashMap;
import java.util.Map;
import org.pieShare.pieShareApp.model.command.SimpleMessageCommand;
import org.pieShare.pieShareApp.service.commandService.SimpleMessageCommandService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.AbstractActionService;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageActionService extends AbstractActionService<SimpleMessageCommand, SimpleMessageCommandService> {
    @Override
    public String getCommandName() {
        return "sendMsg";
    }

    @Override
    public Map<String, Class> getArguments() {
        Map<String, Class> args = new HashMap<>();
        args.put("msg", String.class);
        return args;
    }

    @Override
    public SimpleMessageCommand getCommand(Map<String, Object> args) {
        SimpleMessageCommand command = (SimpleMessageCommand)this.beanService.getBean("simpleMessageCommand");
        command.setMsg((String)args.get("msg"));
        return command;
    }
}
