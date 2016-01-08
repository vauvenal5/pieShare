/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration.PieShareApp;

import org.pieShare.pieShareAppFx.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareAppFx.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.LogoutTask;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.ResetPwdTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileChangedTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileDeletedTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileListTask;
import org.pieShare.pieShareApp.task.eventTasks.FileMetaTask;
import org.pieShare.pieShareApp.task.eventTasks.FileRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.eventTasks.MetaCommitTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.NewFileTask;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderCreateTask;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderDeleteTask;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;
import org.pieShare.pieShareApp.task.localTasks.TorrentTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.ALocalFileEventTask;
import org.pieShare.pieShareAppFx.springConfiguration.ProviderConfiguration;
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
	private PieUtilitiesConfiguration utilities;
	@Autowired
	private ProviderConfiguration providers;

	private void aMessageSendingTask(AMessageSendingTask task) {
		task.setClusterManagementService(this.plate.clusterManagementService());
		task.setMessageFactoryService(this.services.messageFactoryService());
		task.setUserService(services.userService());
	}

	private void aLocalFileEventTask(ALocalFileEventTask task) {
		this.aMessageSendingTask(task);

		task.setFileFilterService(services.fileFilterService());
		task.setHistoryService(services.historyService());
		task.setFileEncrypterService(services.fileEncryptionService());
		task.setFileWatcherService(this.services.apacheFileWatcherService());
		task.setHistoryFileService(this.services.historyFileService());
	}

	@Bean
	@Scope(value = "prototype")
	public FileMetaTask fileMetaTask() {
		FileMetaTask task = new FileMetaTask();
		aMessageSendingTask(task);
		task.setRequestService(this.services.requestService());
		task.setBitTorrentService(this.services.bitTorrentService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileRequestTask fileRequestTask() {
		FileRequestTask task = new FileRequestTask();
		aMessageSendingTask(task);
		task.setBitTorrentService(this.services.bitTorrentService());
		task.setShareService(this.services.shareService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileTransferCompleteTask fileTransferCompleteTask() {
		FileTransferCompleteTask task = new FileTransferCompleteTask();
		task.setBitTorentService(this.services.bitTorrentService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public NewFileTask newFileTask() {
		NewFileTask task = new NewFileTask();
		task.setRequestService(services.requestService());
		task.setComparerService(services.fileCompareService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileChangedTask fileChangedTask() {
		FileChangedTask task = new FileChangedTask();
		task.setRequestService(this.services.requestService());
		task.setComparerService(this.services.fileCompareService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public LocalFileCreatedTask localFileCreatedTask() {
		LocalFileCreatedTask task = new LocalFileCreatedTask();
		this.aLocalFileEventTask(task);
		task.setFileService(this.services.localFileService());
                task.setFolderService(this.services.folderService());
		return task;
	}
        //TODO: Folder Service needed in more methods?

	@Bean
	@Scope(value = "prototype")
	public LocalFileChangedTask localFileChangedTask() {
		LocalFileChangedTask task = new LocalFileChangedTask();
		this.aLocalFileEventTask(task);
		task.setFileService(this.services.localFileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public LocalFileDeletedTask localFileDeletedTask() {
		LocalFileDeletedTask task = new LocalFileDeletedTask();
		this.aLocalFileEventTask(task);
		task.setFileService(this.services.localFileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileListTask fileListTask() {
		FileListTask task = new FileListTask();
		task.setComparerService(this.services.fileCompareService());
		task.setRequestService(this.services.requestService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileListRequestTask fileListRequestTask() {
		FileListRequestTask task = new FileListRequestTask();
		this.aMessageSendingTask(task);
		task.setFileService(this.services.historyFileService());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public FileDeletedTask fileDeletedTask() {
		FileDeletedTask task = new FileDeletedTask();
		task.setFileService(this.services.historyFileService());
		task.setComparerService(this.services.fileCompareService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LoginTask loginTask() {
		LoginTask service = new LoginTask();
		service.setSymmetricEncryptedChannelProvider(this.providers.symmetricEncryptedChannelProvider);
		service.setPasswordEncryptionService(utilities.passwordEncryptionService());
		service.setConfigurationFactory(services.configurationFactory());
		service.setEncodeService(utilities.encodeService());
		service.setDatabaseService(services.databaseService());
		service.setClusterManagementService(plate.clusterManagementService());
		service.setHistoryService(services.historyService());
		service.setFileWatcherService(this.services.apacheFileWatcherService());
		service.setMessageFactoryService(this.services.messageFactoryService());
		service.setFileService(this.services.historyFileService());
		service.setUserService(services.userService());
		return service;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LogoutTask logoutTask() {
		LogoutTask task = new LogoutTask();
		task.setClusterManagementService(plate.clusterManagementService());
		task.setUserService(services.userService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public ResetPwdTask resetPwdTask() {
		ResetPwdTask task = new ResetPwdTask();
		task.setDatabaseService(services.databaseService());
		task.setUserService(services.userService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public TorrentTask torrentTask() {
		TorrentTask task = new TorrentTask();
		this.aMessageSendingTask(task);
		task.setNetworkService(this.utilities.networkService());
		task.setShareService(this.services.shareService());
		task.setShutdownService(this.utilities.shutdownService());
		task.setBitTorrentService(this.services.bitTorrentService());
		task.setFileService(this.services.historyFileService());
		task.setRequestService(this.services.requestService());
		return task;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public MetaCommitTask metaCommitTask() {
		MetaCommitTask task = new MetaCommitTask();
		task.setBitTorrentService(this.services.bitTorrentService());
		task.setShareService(this.services.shareService());
		task.setCompareService(this.services.fileCompareService());
		task.setRequestService(this.services.requestService());
		task.setClusterManagementService(this.plate.clusterManagementService());
		return task;
	}
        
        @Bean
        @Lazy
        @Scope(value = "prototype")
        public FolderCreateTask createFolderTask() {
                FolderCreateTask task = new FolderCreateTask();
                task.setFolderService(this.services.folderService());
                return task;
        }
        
        @Bean
        @Lazy
        @Scope(value = "prototype")
        public FolderDeleteTask deleteFolderTask() {
            FolderDeleteTask task = new FolderDeleteTask();
            task.setFolderService(this.services.folderService());
            return task;
        }
		
	@Bean
	@Lazy
	@Scope(value = "prototype")
	public EventFoldingTimerTask eventFoldingTimerTask() {
		EventFoldingTimerTask task = new EventFoldingTimerTask();
		task.setExecutorService(this.utilities.pieExecutorService());
		task.setLocalFileChangedProvider(this.providers.localFileChangedProvider);
		task.setLocalFileCreatedProvider(this.providers.localFileCreateProvider);
		task.setLocalFileDeletedProvider(this.providers.localFileDeletedProvider);
		return task;
	}
}
