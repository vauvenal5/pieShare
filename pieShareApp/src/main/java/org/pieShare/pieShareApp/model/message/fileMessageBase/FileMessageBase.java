/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.fileMessageBase;

import org.pieShare.pieShareApp.model.message.api.IFileMessageBase;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.AClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 *
 * @author Svetoslav
 */
public class FileMessageBase extends AClusterMessage implements IFileMessageBase {
	protected PieFile file;

	@Override
	public PieFile getPieFile() {
		return file;
	}

	@Override
	public void setPieFile(PieFile file) {
		this.file = file;
	}
}
