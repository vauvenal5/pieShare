/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.fileListenerService;

import java.io.File;
import java.io.IOException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcher implements IFileWatcherService, IShutdownableService {

	private IFileListenerService fileListener;
	
	private File watchDir;
	private DefaultFileMonitor fileMonitor;

	public void setFileListener(IFileListenerService fileListener) {
		this.fileListener = fileListener;
	}

	@Override
	public void setWatchDir(File watchDir) {
		this.watchDir = watchDir;
	}

	public void watchDir() throws IOException {

		FileSystemManager fileSystemManager = VFS.getManager();
		FileObject dirToWatchFO = null;
		dirToWatchFO = fileSystemManager.resolveFile(watchDir.getAbsolutePath());

		fileMonitor = new DefaultFileMonitor(fileListener);

		fileMonitor.setRecursive(true);
		fileMonitor.addFile(dirToWatchFO);
		fileMonitor.start();
	}

	@Override
	public void run() {
		try {
			watchDir();
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Watcher error", ex);
		}
	}

	@Override
	public void shutdown() {
		fileMonitor.stop();
	}
}
