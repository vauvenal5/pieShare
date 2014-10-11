/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareAppFx.springConfiguration.PieShareApp;

import java.beans.Beans;
import org.pieShare.pieShareApp.model.task.FileMetaTask;
import org.pieShare.pieShareApp.model.task.FileRequestTask;
import org.pieShare.pieShareApp.model.task.FileTransferCompleteTask;
import org.pieShare.pieShareApp.model.task.NewFileTask;
import org.pieShare.pieShareApp.service.fileService.task.FileChangedTask;
import org.pieShare.pieShareApp.service.fileService.task.FileCreatedTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppTasks {
        @Autowired
	private PieShareAppService services;
	
	@Bean
	@Lazy
	public FileMetaTask fileMetaTask() {
		FileMetaTask task = new FileMetaTask();
		task.setRequestService(this.services.requestService());
		return task;
	}
	
	@Bean
	@Lazy
	public FileRequestTask fileRequestTask() {
		FileRequestTask task = new FileRequestTask();
		task.setFileService(this.services.fileService());
		return task;
	}
	
	@Bean
	@Lazy
	public FileTransferCompleteTask fileTransferCompleteTask() {
		FileTransferCompleteTask task = new FileTransferCompleteTask();
		task.setShareService(this.services.shareService());
		return task;
	}
	
	@Bean
	@Lazy
	public NewFileTask newFileTask() {
		NewFileTask task = new NewFileTask();
		task.setFileService(this.services.fileService());
		return task;
	}
	
	@Bean
	@Lazy
	public FileCreatedTask fileCreatedTask() {
		FileCreatedTask task = new FileCreatedTask();
		task.setFileService(this.services.fileService());
		return task;
	}
	
	@Bean
	@Lazy
	public FileChangedTask fileChangedTask() {
		FileChangedTask task = new FileChangedTask();
		task.setFileService(this.services.fileService());
		return task;
	}
}
