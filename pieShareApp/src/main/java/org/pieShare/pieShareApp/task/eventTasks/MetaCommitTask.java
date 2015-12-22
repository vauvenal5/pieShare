/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.metaMessage.MetaCommitMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class MetaCommitTask extends PieEventTaskBase<MetaCommitMessage> {
	
	private IShareService shareService;
	private IBitTorrentService bitTorrentService;
	private ILocalFileCompareService compareService;
	private IRequestService requestService;
	private IClusterManagementService clusterManagementService;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}
	
	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setCompareService(ILocalFileCompareService compareService) {
		this.compareService = compareService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	@Override
	public void run() {
		//every meta message we receive needs to be handled!!!
		//this is due to the fact that it could happen that two parallel trackers run!!!
		
		//todo: if not prepared don't do anything for the time being
		//think of this later
		//todo: we need also to do a isRequested check in case we are not the seeder but want the file!
		if(this.shareService.isPrepared((PieFile) this.msg.getPieFolder())) {
			PieLogger.trace(this.getClass(), "Starting to share file {}!", msg.getPieFolder().getName());
			this.bitTorrentService.shareFile(this.msg.getFileMeta());
			return;
		}
		
		if(!this.compareService.isConflictedOrNotNeeded((PieFile) this.msg.getPieFolder()) && this.requestService.handleRequest((PieFile) this.msg.getPieFolder(), true)) {
			PieLogger.trace(this.getClass(), "Starting to handle file {}!", msg.getPieFolder().getName());
			this.bitTorrentService.handleFile(this.msg.getFileMeta());
			try {
				//we have to pass the message commit forward so the server 
				//knows that there is another client on this torrent
				this.clusterManagementService.sendMessage(this.msg);
			} catch (ClusterManagmentServiceException ex) {
				PieLogger.error(this.getClass(), "Passing on MetaCommit failed!", ex);
			}
			return;
		}
		
		PieLogger.debug(this.getClass(), "File {} not prepared or not needed!", this.msg.getPieFolder().getName());
	}
	
}
