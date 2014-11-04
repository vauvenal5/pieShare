/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.task.eventTasks.FileDeletedTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileListTask;
import org.pieShare.pieShareApp.task.eventTasks.FileMetaTask;
import org.pieShare.pieShareApp.task.eventTasks.FileRequestTask;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.eventTasks.NewFileTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
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
		PieUser user = databaseService.findPieUser();
		this.executorFactory.registerTask(FileTransferMetaMessage.class, FileMetaTask.class);
		this.executorFactory.registerTask(FileRequestMessage.class, FileRequestTask.class);
		this.executorFactory.registerTask(NewFileMessage.class, NewFileTask.class);
		this.executorFactory.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);
		this.executorFactory.registerTask(FileListRequestMessage.class, FileListRequestTask.class);
		this.executorFactory.registerTask(FileListMessage.class, FileListTask.class);
		this.executorFactory.registerTask(FileDeletedMessage.class, FileDeletedTask.class);
	}

	public void stop() {
		try {
			this.clusterManagementService.diconnectAll();
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Stop all failed!", ex);
		}

		this.shutdownService.fireShutdown();
	}
}
