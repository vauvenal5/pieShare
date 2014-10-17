/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration.PieShareApp;

import org.pieShare.pieShareApp.task.FileListRequestTask;
import org.pieShare.pieShareApp.task.FileListTask;
import org.pieShare.pieShareApp.task.FileMetaTask;
import org.pieShare.pieShareApp.task.FileRequestTask;
import org.pieShare.pieShareApp.task.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.NewFileTask;
import org.pieShare.pieShareApp.task.FileChangedTask;
import org.pieShare.pieShareApp.task.FileDeletedTask;
import org.pieShare.pieShareAppFx.springConfiguration.PiePlateConfiguration;
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
public class PieShareAppTasks {

	@Autowired
	private PieShareAppService services;
	@Autowired
	private PiePlateConfiguration plate;

	@Bean
	@Scope(value = "prototype")
	public FileMetaTask fileMetaTask() {
		FileMetaTask task = new FileMetaTask();
		task.setRequestService(this.services.requestService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileRequestTask fileRequestTask() {
		FileRequestTask task = new FileRequestTask();
		task.setFileService(this.services.fileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileTransferCompleteTask fileTransferCompleteTask() {
		FileTransferCompleteTask task = new FileTransferCompleteTask();
		task.setShareService(this.services.shareService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public NewFileTask newFileTask() {
		NewFileTask task = new NewFileTask();
		task.setComparerService(this.services.comparerService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public FileChangedTask fileChangedTask() {
		FileChangedTask task = new FileChangedTask();
		task.setFileService(this.services.fileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileListTask fileListTask() {
		FileListTask task = new FileListTask();
		task.setComparerService(this.services.comparerService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileListRequestTask fileListRequestTask() {
		FileListRequestTask task = new FileListRequestTask();
		task.setClusterManagementService(this.plate.clusterManagementService());
		task.setFileService(this.services.fileService());
		return task;
	}
	
	@Bean
	@Scope(value="prototype")
	public FileDeletedTask fileDeletedTask() {
		FileDeletedTask task = new FileDeletedTask();
		task.setFileService(this.services.fileService());
		return task;
	}
}
