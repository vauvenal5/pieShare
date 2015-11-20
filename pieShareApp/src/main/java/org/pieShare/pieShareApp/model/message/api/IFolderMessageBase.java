/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.message.api;

import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;

/**
 *
 * @author daniela
 */
public interface IFolderMessageBase extends IEncryptedMessage{
        PieFolder getPieFolder();

	void setPieFolder(PieFolder folderOrFile);
}
