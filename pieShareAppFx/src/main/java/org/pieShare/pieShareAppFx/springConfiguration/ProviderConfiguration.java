/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration;

import javax.inject.Provider;
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
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleCompleteTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@Configuration
public class ProviderConfiguration {
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
}
