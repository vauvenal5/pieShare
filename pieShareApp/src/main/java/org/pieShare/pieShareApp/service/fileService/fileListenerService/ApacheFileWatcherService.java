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
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcherService extends AShutdownableService implements IFileWatcherService {

	private IBeanService beanService;
	
	private List<DefaultFileMonitor> fileMonitors;
	//todo: does the part with modified files belong in here or maybe even into the listener?
		//if into the listener: how will changes be propageted to the right listener?
	private List<PieFile> modifiedFiles;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}
	
	public void init() {
		this.fileMonitors = new ArrayList();
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

		//IFileListenerService fileListener = this.beanService.getBean(ApacheDefaultFileListener.class);
		DefaultFileMonitor fileMonitor = this.beanService.getBean(DefaultFileMonitor.class);

		fileMonitor.setRecursive(true);
		fileMonitor.addFile(dirToWatchFO);
		fileMonitor.start();
		
		this.fileMonitors.add(fileMonitor);
	}

	@Override
	public void shutdown() {
		for(DefaultFileMonitor fm: this.fileMonitors) {
			fm.stop();
			fm = null;
		}
	}

	@Override
	public boolean isPieFileModifiedByUs(PieFile file) {
		return this.modifiedFiles.contains(file);
	}
}
