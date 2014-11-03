/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.util.ArrayList;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownService;

/**
 *
 * @author Svetoslav
 */
public class PieShareService {

	private IExecutorService executorService;
	private ICommandParserService parserService;
	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;
	private IShutdownService shutdownService;
	private IDatabaseService databaseService;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setShutdownService(IShutdownService shutdownService) {
		this.shutdownService = shutdownService;
	}

	public PieShareService() {
	}

	public void setExecutorService(IExecutorService service) {
		this.executorService = service;
	}

	public void setParserService(ICommandParserService service) {
		this.parserService = service;
	}

	public void setBeanService(IBeanService service) {
		this.beanService = service;
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
