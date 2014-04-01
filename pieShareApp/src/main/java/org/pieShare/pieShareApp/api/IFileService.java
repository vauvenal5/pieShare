/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.api;

import java.io.File;
import org.pieShare.pieShareApp.model.FileChangedMessage;

/**
 *
 * @author richy
 */
public interface IFileService
{

	public void remoteFileChanged(FileChangedMessage message);

	public IFileWatcherService newFolderAdded(File folder);
}
