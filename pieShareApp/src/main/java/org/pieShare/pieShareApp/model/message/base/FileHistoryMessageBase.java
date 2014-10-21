/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.base;

import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Svetoslav
 */
public class FileHistoryMessageBase extends FileMessageBase {
	protected PieFile previousFile;

	public PieFile getPreviousFile() {
		return previousFile;
	}

	public void setPreviousFile(PieFile file) {
		this.previousFile = file;
	}
}
