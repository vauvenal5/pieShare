/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareAppFx.springConfiguration.PieShareApp;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.command.SimpleMessageCommand;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppModel {
	@Bean
	@Lazy
	@Scope(value="prototype")
	public SimpleMessageCommand simpleMessageCommand() {
		return new SimpleMessageCommand();
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileRequestMessage fileRequestMessage() {
		return new FileRequestMessage();
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileTransferCompleteMessage fileTransferCompleteMessage() {
		return new FileTransferCompleteMessage();
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public NewFileMessage newFileMessage() {
		return new NewFileMessage();
	}
        
        @Bean
	@Lazy
	@Scope(value="prototype")
        public FileListMessage fileListMessage() {
            return new FileListMessage();
        }
        
        @Bean
	@Lazy
	@Scope(value="prototype")
        public FileListRequestMessage fileListRequestMessage() {
            return new FileListRequestMessage();
        }
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileDeletedMessage fileDeletedMessage() {
		return new FileDeletedMessage();
	}
	
	@Bean
	@Lazy
	public LoginCommand loginCommand() {
		return new LoginCommand();
	}
	
	@Bean
	@Lazy
	public PieUser pieUser() {
		return new PieUser();
	}
}
