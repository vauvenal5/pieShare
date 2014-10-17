/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileListenerService;

import java.io.File;
import java.io.IOException;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcher implements IFileWatcherService {

	private File watchDir;
	private FileListener fileListener;

	public void setFileListener(FileListener fileListener) {
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

		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(fileListener);

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
}
