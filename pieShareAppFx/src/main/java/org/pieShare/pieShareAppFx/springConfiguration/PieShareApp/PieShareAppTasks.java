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
import org.pieShare.pieShareApp.task.localTasks.ALocalEventTask;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingServiceTask;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;
import org.pieShare.pieShareApp.task.localTasks.TorrentTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.ALocalFileEventTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.ALocalFolderEventTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderDeletedTask;
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
	
	private void aLocalEventTask(ALocalEventTask task) {
		this.aMessageSendingTask(task);
		task.setFileFilterService(this.services.fileFilterService());
		task.setHistoryService(services.historyService());
	}

    private void aLocalFileEventTask(ALocalFileEventTask task) {
        this.aLocalEventTask(task);
        task.setFileWatcherService(this.services.apacheFileWatcherService());
		task.setFileService(this.services.localFileService());
    }
	
	private void aLocalFolderEventTask(ALocalFolderEventTask task) {
		this.aLocalEventTask(task);
		task.setFolderService(this.services.folderService());
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
        task.setComparerService(services.historyCompareService());
        return task;
    }

    @Bean
    @Scope(value = "prototype")
    public FileChangedTask fileChangedTask() {
        FileChangedTask task = new FileChangedTask();
        task.setRequestService(this.services.requestService());
        task.setComparerService(this.services.historyCompareService());
        return task;
    }

    @Bean
    @Scope(value = "prototype")
    public LocalFileCreatedTask localFileCreatedTask() {
        LocalFileCreatedTask task = new LocalFileCreatedTask();
        this.aLocalFileEventTask(task);
        return task;
    }
    //TODO: Folder Service needed in more methods?

    @Bean
    @Scope(value = "prototype")
    public LocalFileChangedTask localFileChangedTask() {
        LocalFileChangedTask task = new LocalFileChangedTask();
        this.aLocalFileEventTask(task);
        return task;
    }

    @Bean
    @Scope(value = "prototype")
    public LocalFileDeletedTask localFileDeletedTask() {
        LocalFileDeletedTask task = new LocalFileDeletedTask();
        this.aLocalFileEventTask(task);
        return task;
    }
	
	@Bean
	@Scope(value = "prototype")
	public LocalFolderCreatedTask localFolderCreatedTask() {
		LocalFolderCreatedTask task = new LocalFolderCreatedTask();
		this.aLocalFolderEventTask(task);
		return task;
	}
	
	@Bean
	@Scope(value = "prototype")
	public LocalFolderDeletedTask localFolderDeletedTask() {
		LocalFolderDeletedTask task = new LocalFolderDeletedTask();
		this.aLocalFolderEventTask(task);
		return task;
	}

    @Bean
    @Scope(value = "prototype")
    public FileListTask fileListTask() {
        FileListTask task = new FileListTask();
        task.setComparerService(this.services.historyCompareService());
        task.setRequestService(this.services.requestService());
		task.setFileService(this.services.localFileService());
		task.setFolderService(this.services.folderService());
        return task;
    }

    @Bean
    @Scope(value = "prototype")
    public FileListRequestTask fileListRequestTask() {
        FileListRequestTask task = new FileListRequestTask();
        this.aMessageSendingTask(task);
        task.setHistoryService(this.services.historyService());
        return task;
    }

    @Bean
    @Scope(value = "prototype")
    public FileDeletedTask fileDeletedTask() {
        FileDeletedTask task = new FileDeletedTask();
        task.setFileService(this.services.localFileService());
        task.setComparerService(this.services.historyCompareService());
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
        service.setFileWatcherService(this.services.apacheFileWatcherService());
        service.setUserService(services.userService());
        service.setUserTools(services.userToolsService());
        return service;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LogoutTask logoutTask() {
        LogoutTask task = new LogoutTask();
        task.setClusterManagementService(plate.clusterManagementService());
        task.setUserService(services.userService());
        task.setUserTools(services.userToolsService());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public ResetPwdTask resetPwdTask() {
        ResetPwdTask task = new ResetPwdTask();
        task.setDatabaseService(services.databaseService());
        task.setUserService(services.userService());
        task.setUserTools(services.userToolsService());
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
        task.setFileService(this.services.localFileService());
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
        task.setCompareService(this.services.historyCompareService());
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
		task.setHistoryService(this.services.historyService());
                task.setFileService(this.services.localFileService());
                task.setHashService(this.utilities.md5Service());
		
		task.setLocalFileChangedProvider(this.providers.localFileChangedProvider);
		task.setLocalFileCreatedProvider(this.providers.localFileCreateProvider);
		task.setLocalFileDeletedProvider(this.providers.localFileDeletedProvider);
                task.setLocalFileRenamedProvider(this.providers.localFileRenamedProvider);
                task.setLocalFileMovedProvider(this.providers.localFileMovedProvider);
		task.setLocalFolderCreatedProvider(this.providers.localFolderCreatedProvider);
		task.setLocalFolderDeletedProvider(this.providers.localFolderDeletedProvider);
                task.setLocalFolderRenamedProvider(this.providers.localFolderRenamedProvider);
                task.setLocalFolderMovedProvider(this.providers.localFolderMovedProvider);
		return task;
	}
        
    @Bean
    @Lazy
    @Scope(value = "prototype")
    public EventFoldingServiceTask eventFoldingServiceTask() {
        EventFoldingServiceTask task = new EventFoldingServiceTask();
        task.setEventFoldingService(this.services.eventFoldingService());
        task.setLocalFileEventProvider(this.providers.localFileEventProvider);
        return task;
    }
        
}
