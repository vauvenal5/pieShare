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
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageAction extends CommandAction {
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
    public IPieMessage getMessage() {
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg((String)this.args.get("msg"));
        return msg;
    }
}
