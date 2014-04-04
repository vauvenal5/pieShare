/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.api;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Richard
 */
public interface IFileWatcherService extends IPieTask
{

	public void setFileMerger(IFileMerger fileMerger);

	public void setWatchDir(File watchDir);

	public void watchDir() throws IOException;

	public void cancel();

	public void deleteAll();
}
