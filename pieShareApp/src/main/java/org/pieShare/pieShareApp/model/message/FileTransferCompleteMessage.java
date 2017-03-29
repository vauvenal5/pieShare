/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 *
 * @author Svetoslav
 */
public class FileTransferCompleteMessage extends HeaderMessage {

	private PieFile pieFile;

	public PieFile getPieFile() {
		return pieFile;
	}

	public void setPieFile(PieFile pieFile) {
		this.pieFile = pieFile;
	}
}