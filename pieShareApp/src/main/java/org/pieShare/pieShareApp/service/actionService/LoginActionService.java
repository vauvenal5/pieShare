/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.actionService;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.commandParser.AbstractActionService;

/**
 *
 * @author Svetoslav
 */
public class LoginActionService extends AbstractActionService<LoginCommand, LoginCommandService> {

    @Override
    public LoginCommand getCommand(Map<String, Object> args) {
        LoginCommand command = (LoginCommand)this.beanService.getBean(PieShareAppBeanNames.getLoginCommmandName());
        command.setUserName((String)args.get("userName"));
        
        System.out.println("Enter password:");
        PlainTextPassword pwd = new PlainTextPassword();
        
        //Console console = System.console();
        //pwd.password = console.readPassword();
        pwd.password = "test".toCharArray();
        command.setPlainTextPassword(pwd);
        
        return command;
    }

    @Override
    public String getCommandName() {
        return "login";
    }

    @Override
    public Map<String, Class> getArguments() {
        Map<String, Class> args = new HashMap<>();
        args.put("userName", String.class);
        return args;
    }
    
}
