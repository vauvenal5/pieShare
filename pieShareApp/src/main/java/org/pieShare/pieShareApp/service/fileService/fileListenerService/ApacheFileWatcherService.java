/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.fileListenerService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcherService implements IFileWatcherService, IClusterAddedListener {

	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;
	
	private List<DefaultFileMonitor> fileMonitors;
	//todo: does the part with modified files belong in here or maybe even into the listener?
		//if into the listener: how will changes be propageted to the right listener?
	private List<PieFile> modifiedFiles;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}
	
	public void init() {
		this.fileMonitors = new ArrayList();
		this.clusterManagementService.getClusterAddedEventBase().addEventListener(this);
		this.modifiedFiles = Collections.synchronizedList(new ArrayList<>());
	}
	
	@Override
	public void addPieFileToModifiedList(PieFile pieFile) {
		this.modifiedFiles.add(pieFile);
	}
	
	@Override
	public boolean removePieFileFromModifiedList(PieFile file) {
		return this.modifiedFiles.remove(file);
	}

	@Override
	public void watchDir(File file) throws IOException {

		FileSystemManager fileSystemManager = VFS.getManager();
		FileObject dirToWatchFO = null;
		dirToWatchFO = fileSystemManager.resolveFile(file.getAbsolutePath());

		IFileListenerService fileListener = this.beanService.getBean(ApacheDefaultFileListener.class);
		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(fileListener);

		fileMonitor.setRecursive(true);
		fileMonitor.addFile(dirToWatchFO);
		fileMonitor.start();
		
		this.fileMonitors.add(fileMonitor);
	}
	
	@Override
	public void handleObject(ClusterAddedEvent event) {
		try {
			//when a cluster is added this actually means that this client has entered a cloud
			PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			PieShareConfiguration configuration = user.getPieShareConfiguration();
			this.watchDir(configuration.getWorkingDir());
			
			//todo: who is responsible for this message?
			this.clusterManagementService.sendMessage(new FileListRequestMessage(), user.getCloudName(), user.getPassword());
		}
		catch (ClusterManagmentServiceException ex) {
			//todo: error handling
			PieLogger.error(this.getClass(), "File error.", ex);
		}
		catch(IOException ex) {
			PieLogger.error(this.getClass(), "File error.", ex);
		}
		//todo: unite FileService, CompareService and all other regarding FileHandling in one package
	}

	@Override
	public void shutdown() {
		for(DefaultFileMonitor fm: this.fileMonitors) {
			fm.stop();
		}
	}
}
