/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.springConfiguration.PieShareApp;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.eventTasks.FileDeletedTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListTask;
import org.pieShare.pieShareApp.task.eventTasks.FileMetaTask;
import org.pieShare.pieShareApp.task.eventTasks.FileRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.eventTasks.NewFileTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.base.FileEventTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.LogoutTask;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.ResetPwdTask;
import org.pieShare.pieShareApp.task.localTasks.ComparePieFileTask;
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
	@Autowired
	private PieUtilitiesConfiguration config;

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
		task.setFileUtilsService(this.services.fileUtilsService());
		task.setHashService(this.config.md5Service());
		task.setPieAppConfig(this.services.pieShareAppConfiguration());
		task.setRequestService(this.services.requestService());
		task.setShareService(this.services.shareService());
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

	private void fileEventTask(FileEventTask task) {
		task.setBeanService(this.config.beanService());
		task.setClusterManagementService(this.plate.clusterManagementService());
		task.setFileFilterService(services.fileFilterService());
		task.setFileUtilsService(this.services.fileUtilsService());
	}

	@Bean
	@Scope(value = "prototype")
	public LocalFileCreatedTask localFileCreatedTask() {
		LocalFileCreatedTask task = new LocalFileCreatedTask();
		this.fileEventTask(task);
		task.setFileService(this.services.fileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public LocalFileChangedTask fileChangedTask() {
		LocalFileChangedTask task = new LocalFileChangedTask();
		this.fileEventTask(task);
		task.setFileService(this.services.fileService());
		task.setFileListener(this.services.fileListenerService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public LocalFileDeletedTask localFileDeletedTask() {
		LocalFileDeletedTask task = new LocalFileDeletedTask();
		this.fileEventTask(task);
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileListTask fileListTask() {
		FileListTask task = new FileListTask();
		task.setBeanService(this.config.beanService());
		task.setExecutorService(this.config.pieExecutorService());
		return task;
	}
	
	@Bean
	@Scope(value = "prototype")
	public ComparePieFileTask comparePieFileTask() {
		ComparePieFileTask task = new ComparePieFileTask();
		task.setComparerService(this.services.comparerService());
		task.setRequestService(this.services.requestService());
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
	@Scope(value = "prototype")
	public FileDeletedTask fileDeletedTask() {
		FileDeletedTask task = new FileDeletedTask();
		task.setFileService(this.services.fileService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LoginTask loginTask() {
		LoginTask service = new LoginTask();
		service.setBeanService(config.beanService());
		service.setPasswordEncryptionService(config.passwordEncryptionService());
		service.setPieShareAppConfig(services.pieShareAppConfiguration());
		service.setEncodeService(config.encodeService());
		service.setDatabaseService(services.databaseService());
		service.setClusterManagementService(plate.clusterManagementService());
		return service;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LogoutTask logoutTask() {
		LogoutTask task = new LogoutTask();
		task.setBeanService(config.beanService());
		task.setClusterManagementService(plate.clusterManagementService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public ResetPwdTask resetPwdTask() {
		ResetPwdTask task = new ResetPwdTask();
		task.setBeanService(config.beanService());
		task.setConfig(services.pieShareAppConfiguration());
		task.setDatabaseService(services.databaseService());
		return task;
	}
}
