/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareAppFx.springConfiguration.PieShareApp;

import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.command.SimpleMessageCommand;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Svetoslav
 */
@Lazy
@Configuration
public class PieShareAppModel {	
	@Bean
	@Scope(value="prototype")
	public SimpleMessageCommand simpleMessageCommand() {
		return new SimpleMessageCommand();
	}
	
	@Bean
	public LoginCommand loginCommand() {
		return new LoginCommand();
	}
	
	@Bean
	public PieUser pieUser() {
		return new PieUser();
	}
	
	@Bean
	@Scope(value = "prototype")
	public PieShareConfiguration pieShareConfiguration() {
		PieShareConfiguration config = new PieShareConfiguration();
		return config;
	}
	
	@Bean
	@Scope(value = "prototype")
	public PieFileEntity pieFileEntity() {
		return new PieFileEntity();
	}
}
