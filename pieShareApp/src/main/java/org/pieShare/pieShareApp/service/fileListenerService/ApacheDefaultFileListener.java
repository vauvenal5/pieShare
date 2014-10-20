/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileListenerService;

import java.io.File;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.task.localTasks.FileCopyObserverTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements FileListener {

	//private IFileObserver fileObserver;
	private IExecutorService executerService;
	private IBeanService beanService;
	private IFileUtilsService utilsService;
	private IClusterManagementService clusterManagementService;

	/*public void setFileObserver(IFileObserver fileObserver) {
		this.fileObserver = fileObserver;
	}*/

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setUtilsService(IFileUtilsService utilsService) {
		this.utilsService = utilsService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File created: {}", filePath);
		FileCopyObserverTask observerTask = beanService.getBean(PieShareAppBeanNames.getFileCopyObserverTask());
		observerTask.setFile(new File(filePath));
		executerService.execute(observerTask);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		//todo: does the file delete comand also has to wait like file created until the delete has finished?
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File deleted: {}", filePath);
		//todo: for the time being we will just delete without checks
		//later somekinde of persistency and check has to be added
		FileDeletedMessage msg = beanService.getBean(PieShareAppBeanNames.getFileDeletedMessage());
		msg.setFile(this.utilsService.getPieFile(new File(filePath)));
		//todo: need somewhere a match between working dir and belonging cloud
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		
		this.clusterManagementService.sendMessage(msg, user.getCloudName());
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File changed: {}", filePath);
		/*FileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
		File file = new File(filePath);
		task.setCreatedFile(file);
		startObservation(file, task);*/
	}
}
