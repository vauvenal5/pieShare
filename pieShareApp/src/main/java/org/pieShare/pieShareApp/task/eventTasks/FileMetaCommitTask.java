/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.message.api.IMetaCommitMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieShareApp.service.shareService.NoLocalFileException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileMetaCommitTask extends PieEventTaskBase<IMetaCommitMessage> {
	
	private IShareService shareService;
	private IBitTorrentService bitTorrentService;
	private IFileService fileService;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}
	
	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	@Override
	public void run() {
		//every meta message we receive needs to be handled!!!
		//this is due to the fact that it could happen that two parallel trackers run!!!
		
		//todo: first check if file is prepared
		this.shareService.
		
		//todo: then check if local file exists
		
		//todo: then check if local file equals the file in the metaInfo
		
		File localTmpFile;
		try {
			localTmpFile = this.shareService.prepareFile(msg.getPieFile());
		} catch (NoLocalFileException ex) {
			PieLogger.info(this.getClass(), "Local file doesn't exist.", ex);
			localTmpFile = this.shareService.handleFile(msg.getPieFile());
		}
		
		this.bitTorrentService.handleShareTorrent(msg.getPieFile(), msg.getMetaInfo(), localTmpFile.getParentFile());
	}
	
}
