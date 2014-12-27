/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class TorrentTask implements IPieTask, IShutdownableService {
	
	private IShareService shareService;
	private INetworkService networkService;
	private IBitTorrentService bitTorrentService;
	
	private Client client;
	private PieFile file;
	private SharedTorrent torrent;
	private boolean shutdown;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}

	
	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setFile(PieFile file) {
		this.file = file;
	}

	public void setTorrent(SharedTorrent torrent) {
		this.torrent = torrent;
	}

	@Override
	public void run() {
		try {
			//this.fileWatcherService.addPieFileToModifiedList(pieFile);
			//todo: handle ports out problem!!!
			//todo: this should run somehow over the beans
			this.client = new Client(networkService.getLocalHost(), torrent);

			//todo: this time has to move into the properties
			//todo: won't work for server and client the same way: problem with this timeout the server
			//shuts down after 30 seconds... implement other timeout strategy or rerequest messages
			//reregquest is maybe the better way
			client.share(30);

			//client.waitForCompletion();
			while (!Client.ClientState.DONE.equals(client.getState())) {

				if (this.shutdown) {
					client.stop();
					return;
				}

				// Check if there's an error
				if (Client.ClientState.ERROR.equals(client.getState())) {
					client.stop();
					//todo: ports release when exception
					throw new Exception("ttorrent client Error State");
				}
				
				if (Client.ClientState.SEEDING.equals(client.getState()) 
						&& !this.shareService.isShareActive(this.file)) {
					client.stop();
				}

				// Display statistics
				PieLogger.debug(this.getClass(), "{} %% - state {} - {} bytes downloaded - {} bytes uploaded - {}",
						torrent.getCompletion(), client.getState(), torrent.getDownloaded(), 
						torrent.getUploaded(), file.getFileName());

				// Wait one second
				Thread.sleep(1000);
			}
			
			this.client.stop(false);

			boolean seeder = this.torrent.isSeeder();
			this.bitTorrentService.torrentClientDone(seeder);
			this.shareService.localFileTransferComplete(file, seeder);
		} catch (IOException ex) {
			Logger.getLogger(TorrentTask.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(TorrentTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void shutdown() {
		this.shutdown = true;
	}
	
}
