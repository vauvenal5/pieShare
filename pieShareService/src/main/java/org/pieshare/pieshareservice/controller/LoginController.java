/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareservice.controller;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@RestController
public class LoginController {
	
	@Autowired
	private PieExecutorService executorService;
	@Autowired
	private IBeanService beanService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String greeting() {
		return "Welcome to your local PieShare backbone server!";
	}
	
	private String tmpResult;
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestBody LoginCommand cmd) {
		//the difference between the GUI and the CLI is that we do not
		//want the GUI to block but we do want the CLI to block
		//todo: think freshly about this one more time
			//especially how we could make CLI and GUI go the same way
		LoginTask task = beanService.getBean(LoginTask.class);
		task.setEvent(cmd);
		cmd.setCallback(new ILoginFinished() {

			@Override
			public void error(Exception ex) {
				tmpResult = "An unexpected exception occured. Please refer to the logs!";
			}

			@Override
			public void wrongPassword(WrongPasswordException ex) {
				tmpResult = "Wrong password!";
			}

			@Override
			public void OK() {
				tmpResult = "Login successfull!";
			}
		});
		task.run();
		return tmpResult;
	}
}
