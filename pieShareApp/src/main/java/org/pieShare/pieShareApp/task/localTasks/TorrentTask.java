/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.model.pieFile.FileMeta;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class TorrentTask extends AMessageSendingTask implements IShutdownableService {

	//services
	private IShareService shareService;
	private INetworkService networkService;
	private IBitTorrentService bitTorrentService;
	private IFileService fileService;
	private IRequestService requestService;

	//injected fields
	private FileMeta fileMeta;
	private Torrent torrent;

	//local fields
	private Client client;
	private boolean shutdown;
	private Tracker tracker;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFile(FileMeta file) {
		this.fileMeta = file;
	}

	public void setTorrent(Torrent torrent) {
		this.torrent = torrent;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	@Override
	public void run() {
		PieLogger.trace(this.getClass(), "Starting torrent task for {}.", this.fileMeta.getFile());
		try {
			boolean seeder = this.torrent.isSeeder();
			File destDir = this.fileService.getAbsoluteTmpPath(this.fileMeta.getFile()).toFile().getParentFile();

			//todo: ther is a bug when triing to share 0 byte files
			//start a tracker for each file seperately
			//this is a workaround for the time being due to ttorrent bugs.
			if (seeder) {
				for (List<URI> list : torrent.getAnnounceList()) {
					for (URI uri : list) {
						if (uri.getHost().equals(this.networkService.getLocalHost().getHostAddress())) {
							this.networkService.freeReservedPort(uri.getPort());
							this.tracker = new Tracker(new InetSocketAddress(this.networkService.getLocalHost(), uri.getPort()));
							//todo: security issues?
							this.tracker.announce(new TrackedTorrent(this.torrent));
							this.tracker.start();
						}
					}
				}
			}

			//this.fileWatcherService.addPieFileToModifiedList(pieFile);
			//todo: handle ports out problem!!!
			//todo: this should run somehow over the beans
			SharedTorrent sharedTorrent = new SharedTorrent(this.torrent, destDir);
			this.client = new Client(networkService.getLocalHost(), sharedTorrent);

			//todo: this time has to move into the properties
			//todo: won't work for server and client the same way: problem with this timeout the server
			//shuts down after 30 seconds... implement other timeout strategy or rerequest messages
			//reregquest is maybe the better way
			//todo: the problem is like follows: what happens if we never receive a fileShareCompleteMessage
			//we have to recover somehow
			client.share();

			//todo: errorState discovery is just a dirty fix for bug in ttorrent library!!
			boolean loopDone = false;
			long lastAmount = 0;
			int errorSeconds = 0;
			int sleepTime = 1000;
			int errorThreshold = 15000/sleepTime;
			boolean errorState = false;

			while (!Client.ClientState.DONE.equals(client.getState()) && !loopDone) {

				// Wait one second
				Thread.sleep(sleepTime);

				// Display statistics
				PieLogger.debug(this.getClass(), "{} %% - state {} - {} bytes downloaded - {} bytes uploaded - peers {} - {}",
						sharedTorrent.getCompletion(), client.getState(), sharedTorrent.getDownloaded(),
						sharedTorrent.getUploaded(), client.getPeers().size(), fileMeta.getFile().getFileName());

				if (this.shutdown) {
					PieLogger.info(this.getClass(), String.format("Shuting down torrent task for %s", fileMeta.getFile().getFileName()));
					return;
				}

				// Check if there's an error
				if (Client.ClientState.ERROR.equals(client.getState())) {
					this.shutdown();
					//todo: ports release when exception
					throw new Exception("ttorrent client Error State");
				}

				if ((seeder || Client.ClientState.SEEDING.equals(client.getState())) && !this.bitTorrentService.isShareActive(this.fileMeta)) {
					PieLogger.debug(this.getClass(), String.format("Stoping client for %s by check done loop!", fileMeta.getFile().getFileName()));
					loopDone = true;
				}

				long currentState = sharedTorrent.getDownloaded();

				if (seeder) {
					currentState = sharedTorrent.getUploaded();
				}

				if (currentState == lastAmount) {
					errorSeconds++;
				} else {
					errorSeconds = 0;
				}

				if (errorSeconds > errorThreshold) {
					loopDone = true;
					errorState = true;
				}

				lastAmount = currentState;
			}

			this.shutdown();

			this.bitTorrentService.torrentClientDone(seeder, this.fileMeta.getFile());
			this.shareService.localFileTransferComplete(fileMeta.getFile(), seeder);
			this.requestService.deleteRequestedFile(this.fileMeta.getFile());

			if (!errorState && !seeder) {
				IFileTransferCompleteMessage msgComplete = this.messageFactoryService.getFileTransferCompleteMessage();
				msgComplete.setPieFile(fileMeta.getFile());
				this.setDefaultAdresse(msgComplete);
				this.clusterManagementService.sendMessage(msgComplete);
			}

			if (errorState && !seeder) {
				this.requestService.requestFile(this.fileMeta.getFile());
			}

		} catch (Exception ex) {
			PieLogger.error(this.getClass(), "Exception in torrent task.", ex);
		}
	}

	@Override
	public void shutdown() {
		this.shutdown = true;

		if (this.client != null) {
			this.client.stop(false);
		}

		if (this.tracker != null) {
			this.tracker.stop();
		}
	}

}
