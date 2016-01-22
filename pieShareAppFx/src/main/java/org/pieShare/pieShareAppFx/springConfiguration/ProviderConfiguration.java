/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration;

import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.LogoutTask;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.ResetPwdTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileMetaTask;
import org.pieShare.pieShareApp.task.eventTasks.FileRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.eventTasks.MetaCommitTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileChangedTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileDeletedTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.FileListTask;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.NewFileTask;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderCreateTask;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderDeleteTask;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;
import org.pieShare.pieShareApp.task.localTasks.TorrentTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileRenamedTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderDeletedTask;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleCompleteMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleCompleteTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@Configuration
public class ProviderConfiguration {

	//only providers that provide against an interface need a function returning the provider with a fixed type
	//all others can be auto generated like in here
	//todo: however we need to check if some of those need interfaces!!!!
	@Autowired
	public Provider<FileMetaTask> fileMetaTaskProvider;
	@Autowired
	public Provider<FileRequestTask> fileRequestTaskProvider;
	@Autowired
	public Provider<NewFileTask> newFileTaskProvider;
	@Autowired
	public Provider<FileTransferCompleteTask> fileTransferCompleteTaskProvider;
	@Autowired
	public Provider<FileListRequestTask> fileListRequestTaskProvider;
	@Autowired
	public Provider<FileListTask> fileListTaskProvider;
	@Autowired
	public Provider<FileDeletedTask> fileDeletedTaskProvider;
	@Autowired
	public Provider<FileChangedTask> fileChangedTaskProvider;
	@Autowired
	public Provider<MetaCommitTask> metaCommitTaskProvider;
	@Autowired
	public Provider<LoginTask> loginTaskProvider;
	@Autowired
	public Provider<LogoutTask> logoutTaskProvider;
	@Autowired
	public Provider<ResetPwdTask> resetPwdTaskProvider;
	@Autowired
	public Provider<LoopHoleConnectionTask> loopHoleConnectionTaskProvider;
	@Autowired
	public Provider<LoopHolePuncherTask> loopHolePuncherTaskProvider;
	@Autowired
	public Provider<LoopHoleAckTask> loopHoleAckTaskProvider;
	@Autowired
	public Provider<LoopHoleCompleteTask> loopHoleCompleteTaskProvider;

	@Autowired
	public Provider<ChannelTask> channelTaskProvider;
	@Autowired
	public Provider<LoopHoleListenerTask> loopHoleListenerTaskProvider;
	@Autowired
	public Provider<RegisterMessage> registerMessageProvider;
	@Autowired
	public Provider<LoopHoleAckMessage> loopHoleAckMessageProvider;
	@Autowired
	public Provider<LoopHoleCompleteMessage> loopHoleCompleteMessageProvider;
	@Autowired
	public Provider<LoopHolePunchMessage> loopHolePunchMessageProvider;

	@Autowired
	public Provider<PieFile> pieFileProvider;
	@Autowired
	public Provider<TorrentTask> torrentTaskProvider;
	@Autowired
	public Provider<PieShareConfiguration> pieShareConfigurationProvider;
	@Autowired
	public Provider<SymmetricEncryptedChannel> symmetricEncryptedChannelProvider;

	@Autowired
	public Provider<FolderCreateMessage> folderCreateMessageProvider;
	@Autowired
	public Provider<FolderCreateTask> folderCreateTaskProvider;
	@Autowired
	public Provider<PieFolder> pieFolderProvider;
	@Autowired
	public Provider<FolderDeleteMessage> folderDeleteMessageProvider;
	@Autowired
	public Provider<FolderDeleteTask> folderDeleteTaskProvider;

	@Autowired
	public Provider<LocalFileEvent> localFileEventProvider;
	
	@Autowired
	public Provider<LocalFileCreatedTask> localFileCreateProvider;
	@Autowired
	public Provider<LocalFileChangedTask> localFileChangedProvider;
	@Autowired
	public Provider<LocalFileDeletedTask> localFileDeletedProvider;
        @Autowired
	public Provider<LocalFileRenamedTask> localFileRenamedProvider;
	@Autowired
	public Provider<LocalFolderCreatedTask> localFolderCreatedProvider;
	@Autowired
	public Provider<LocalFolderDeletedTask> localFolderDeletedProvider;
	@Autowired
	public Provider<EventFoldingTimerTask> eventFoldingTimerTaskProvider;
}
