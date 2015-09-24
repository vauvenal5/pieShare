/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.controller;

import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String greeting() {
		return "Welcome to your local PieShare backbone server!";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public void login(LoginCommand cmd) {
		try {
			this.executorService.handlePieEvent(cmd);
		} catch (PieExecutorTaskFactoryException ex) {
			//todo: error handling
		}
	}
}
