/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.fileHistoryMessage;

import org.pieShare.pieShareApp.model.message.api.IFileHistoryMessageBase;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileMessageBase;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;

/**
 *
 * @author Svetoslav
 * TODO: same needed for Folders - common base Filder
 */
public class FileHistoryMessageBase extends FileMessageBase implements IFileHistoryMessageBase {
	protected PieFile previousFile;

	@Override
	public PieFile getPreviousFile() {
		return previousFile;
	}

	@Override
	public void setPreviousFile(PieFile file) {
		this.previousFile = file;
	}
}
