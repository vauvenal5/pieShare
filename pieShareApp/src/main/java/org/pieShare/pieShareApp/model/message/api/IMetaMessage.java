/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.api;

import org.pieShare.pieShareApp.model.pieFile.FileMeta;

/**
 *
 * @author Svetoslav
 */
public interface IMetaMessage extends IFileMessageBase {
	
	FileMeta getFileMeta();
	
	void setFileMeta(FileMeta fileMeta);

	byte[] getMetaInfo();

	void setMetaInfo(byte[] metaInfo);
}
