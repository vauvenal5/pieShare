/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.metaMessage;

import org.pieShare.pieShareApp.model.message.api.IMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.FileMeta;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieTools.piePlate.model.message.AClusterMessage;

/**
 *
 * @author Svetoslav
 */
public abstract class AMetaMessage extends AClusterMessage implements IMetaMessage {
	private FileMeta fileMeta;
	

	public byte[] getMetaInfo() {
		return this.fileMeta.getData();
	}

	@Override
	public void setMetaInfo(byte[] metaInfo) {
		this.fileMeta.setData(metaInfo);
	}

	@Override
	public FileMeta getFileMeta() {
		return this.fileMeta;
	}

	@Override
	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;
	}

	@Override
	public PieFile getPieFolder() {
		return this.fileMeta.getFile();
	}

	@Override
	public void setPieFolder(PieFolder folderOrFile) {
		this.fileMeta.setFile((PieFile)folderOrFile);
	}
}
