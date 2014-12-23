/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.springConfiguration.PieShareApp;

import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author sveto_000
 */
public class PieShareAppMessages {
	
	protected <P extends IPieMessage> P prepareMessage(P message) {
		IPieAddress ad = new JGroupsPieAddress();
		message.setAddress(ad);
		return message;
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileRequestMessage fileRequestMessage() {
		return this.prepareMessage(new FileRequestMessage());
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileTransferCompleteMessage fileTransferCompleteMessage() {
		return this.prepareMessage(new FileTransferCompleteMessage());
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public NewFileMessage newFileMessage() {
		return this.prepareMessage(new NewFileMessage());
	}
        
    @Bean
	@Lazy
	@Scope(value="prototype")
	public FileListMessage fileListMessage() {
		return this.prepareMessage(new FileListMessage());
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileListRequestMessage fileListRequestMessage() {
		return this.prepareMessage(new FileListRequestMessage());
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
	public FileDeletedMessage fileDeletedMessage() {
		return this.prepareMessage(new FileDeletedMessage());
	}
	
	@Bean
	@Lazy
	public FileChangedMessage fileChangedMessage() {
		return this.prepareMessage(new FileChangedMessage());
	}
}
