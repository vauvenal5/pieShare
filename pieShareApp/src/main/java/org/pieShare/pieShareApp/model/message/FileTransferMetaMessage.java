/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieShareApp.model.message.api.IFileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileMessageBase;
import org.pieShare.pieShareApp.model.message.api.IFileMessageBase;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;

/**
 *
 * @author Svetoslav
 */
public class FileTransferMetaMessage extends FileMessageBase implements IFileTransferMetaMessage{

	private byte[] metaInfo;
	private PieFile tmpFile;

	@Override
	public byte[] getMetaInfo() {
		return metaInfo;
	}

	@Override
	public void setMetaInfo(byte[] metaInfo) {
		this.metaInfo = metaInfo;
	}

	@Override
	public PieFile getTmpFile() {
		return this.tmpFile;
	}

	@Override
	public void setTmpFile(PieFile file) {
		this.tmpFile = file;
	}
}
