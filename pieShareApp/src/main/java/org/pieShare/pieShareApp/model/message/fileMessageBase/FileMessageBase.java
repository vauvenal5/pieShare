/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message.fileMessageBase;

import org.pieShare.pieShareApp.model.message.api.IFilderMessageBase;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieTools.piePlate.model.message.AClusterMessage;

/**
 *
 * @author Svetoslav
 */
public class FileMessageBase extends AClusterMessage implements IFilderMessageBase<PieFile> {
	protected PieFile file;

	@Override
	public PieFile getPieFilder() {
		return file;
	}

        @Override
        public void setPieFilder(PieFile folderOrFile) {
		this.file = folderOrFile;
        }
}
