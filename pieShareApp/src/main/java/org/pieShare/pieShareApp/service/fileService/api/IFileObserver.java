/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author Richard
 */
public interface IFileObserver extends IPieTask {

	public void setData(File file);

	public void setTask(IPieTask task);
}
