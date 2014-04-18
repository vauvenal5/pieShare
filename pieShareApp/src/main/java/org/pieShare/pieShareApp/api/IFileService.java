/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.api;

import org.pieShare.pieShareApp.model.message.AllFilesSyncMessage;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;

/**
 *
 * @author richy
 */
public interface IFileService
{

	public void remoteFileChange(FileChangedMessage message);

	public void localFileChange(FileChangedMessage message);

	public void remoteAllFilesSyncRequest(AllFilesSyncMessage msg);

	public void sendAllFilesSyncRequest();
}
