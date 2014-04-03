/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.action;

import java.util.HashMap;
import java.util.Map;
import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieTools.pieCeption.service.action.CommandAction;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageAction extends CommandAction {

    @Override
    public void doAction(Map<String, Object> args) {
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg((String)args.get("msg"));
        this.commitPieMessage(msg);
    }

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
    
}
