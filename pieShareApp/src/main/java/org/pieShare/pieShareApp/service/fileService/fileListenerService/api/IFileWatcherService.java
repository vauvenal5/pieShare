/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.fileListenerService.api;

import java.io.File;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;

/**
 *
 * @author Richard
 */
public interface IFileWatcherService extends IPieTask {

	public void setWatchDir(File watchDir);
}
