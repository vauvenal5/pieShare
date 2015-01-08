/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.util.ArrayList;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.model.command.ResetPwdCommand;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileChangedMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileDeletedMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.NewFileMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.LogoutTask;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.ResetPwdTask;
import org.pieShare.pieShareApp.task.eventTasks.FileChangedTask;
import org.pieShare.pieShareApp.task.eventTasks.FileDeletedTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListTask;
import org.pieShare.pieShareApp.task.eventTasks.FileMetaTask;
import org.pieShare.pieShareApp.task.eventTasks.FileRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.eventTasks.NewFileTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownService;

/**
 *
 * @author Svetoslav
 */
public class PieShareService {

	private PieExecutorTaskFactory executorFactory;
	private IClusterManagementService clusterManagementService;
	private IShutdownService shutdownService;
	private IDatabaseService databaseService;
	private IConfigurationFactory configurationFactory;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setShutdownService(IShutdownService shutdownService) {
		this.shutdownService = shutdownService;
	}

	public void setExecutorFactory(PieExecutorTaskFactory executorFactory) {
		this.executorFactory = executorFactory;
	}

	public void setClusterManagementService(IClusterManagementService service) {
		this.clusterManagementService = service;
	}

	public void setConfigurationFactory(IConfigurationFactory configurationFactory) {
		this.configurationFactory = configurationFactory;
	}

	public void start() {
		//this.executorService.registerTask(SimpleMessage.class, PrintEventTask.class);

		/*
		 //unimportant for the time being because we don't have commandline support
		 try {
		 //todo-sv: change this!!! (new should not be used here)
		 //getbean per class ist dumm... zerst?rt unabh?ngigkeit
		 //SimpleMessageActionService action = this.beanService.getBean(SimpleMessageActionService.class);
		 //this.parserService.registerAction(action);
		 LoginActionService laction = this.beanService.getBean(PieShareAppBeanNames.getLoginActionServiceName());
		 this.parserService.registerAction(laction);
		 } catch (Exception ex) {
		 ex.printStackTrace();
		 }*/
		PieUser user = null;
		ArrayList<PieUser> users = databaseService.findAllPieUser();
		if (users != null && users.size() > 0) {
			user = users.get(0);
		}

		if (user == null) {
			user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		}
		
		user.setPieShareConfiguration(configurationFactory.checkAndCreateConfig(user.getPieShareConfiguration(), false));

		this.executorFactory.registerTask(FileTransferMetaMessage.class, FileMetaTask.class);
		this.executorFactory.registerTask(FileRequestMessage.class, FileRequestTask.class);
		this.executorFactory.registerTask(NewFileMessage.class, NewFileTask.class);
		this.executorFactory.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);
		this.executorFactory.registerTask(FileListRequestMessage.class, FileListRequestTask.class);
		this.executorFactory.registerTask(FileListMessage.class, FileListTask.class);
		this.executorFactory.registerTask(FileDeletedMessage.class, FileDeletedTask.class);
		this.executorFactory.registerTask(FileChangedMessage.class, FileChangedTask.class);

		this.executorFactory.registerTask(LoginCommand.class, LoginTask.class);
		this.executorFactory.registerTask(LogoutCommand.class, LogoutTask.class);
		this.executorFactory.registerTask(ResetPwdCommand.class, ResetPwdTask.class);
	}

	public void stop() {
		try {
			this.clusterManagementService.diconnectAll();
		}
		catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Stop all failed!", ex);
		}

		this.shutdownService.fireShutdown();
	}
}
