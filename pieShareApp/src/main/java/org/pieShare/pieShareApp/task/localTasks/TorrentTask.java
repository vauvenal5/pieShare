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
import java.util.Timer;
import java.util.TimerTask;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.model.pieFile.FileMeta;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class TorrentTask extends AMessageSendingTask implements IShutdownableService {
	
	private IShareService shareService;
	private INetworkService networkService;
	private IBitTorrentService bitTorrentService;
	
	private Client client;
	private FileMeta fileMeta;
	private SharedTorrent torrent;
	private boolean shutdown;
	private Timer timer;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}
	
	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setFile(FileMeta file) {
		this.fileMeta = file;
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
			//todo: the problem is like follows: what happens if we never receive a fileShareCompleteMessage
			//we have to recover somehow
			boolean seeder = this.torrent.isSeeder();
			client.share();
			/*this.timer = new Timer(true);
			
			//todo: maybe instead of using a timer task move this to the while
			//should work because we start sharing after the metacommitherefor there should be an active share
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						System.out.println("InTIMMER!\n");
						boolean shareActive = bitTorrentService.isShareActive(fileMeta);
						System.out.println(String.valueOf(shareActive));
						PieLogger.trace(this.getClass(), "Checking stop conditions for {} seeder: {} ShareActive: {}", fileMeta.getFile().getFileName(), seeder, shareActive);
						if(seeder && !shareActive) {
							PieLogger.debug(this.getClass(), String.format("Stoping client for %s by timer!", fileMeta.getFile().getFileName()));
							client.stop(true);
						}
					} catch(Exception e) {
						PieLogger.error(this.getClass(), "Torrent Timer Task crashed!", e);
					}
				}
			}, 60000, 30000);*/

			//client.waitForCompletion();
			while (!Client.ClientState.DONE.equals(client.getState())) {
				
				// Display statistics
				PieLogger.debug(this.getClass(), "{} %% - state {} - {} bytes downloaded - {} bytes uploaded - {}",
						torrent.getCompletion(), client.getState(), torrent.getDownloaded(), 
						torrent.getUploaded(), fileMeta.getFile().getFileName());
				
				if (this.shutdown) {
					PieLogger.info(this.getClass(), String.format("Shuting down torrent task for %s", fileMeta.getFile().getFileName()));
					client.stop();
					return;
				}

				// Check if there's an error
				if (Client.ClientState.ERROR.equals(client.getState())) {
					client.stop();
					//todo: ports release when exception
					throw new Exception("ttorrent client Error State");
				}
				
				if (seeder && !this.bitTorrentService.isShareActive(this.fileMeta)) {
					PieLogger.debug(this.getClass(), String.format("Stoping client for %s by check done loop!", fileMeta.getFile().getFileName()));
					client.stop(true);
				}
				
				if (Client.ClientState.SEEDING.equals(client.getState()) 
						&& !this.bitTorrentService.isShareActive(this.fileMeta)) {
					PieLogger.debug(this.getClass(), String.format("Stoping client for %s by check done loop!", fileMeta.getFile().getFileName()));
					client.stop();
				}

				// Wait one second
				Thread.sleep(1000);
			}
			
			this.client.stop(false);

			this.bitTorrentService.torrentClientDone(seeder);
			this.shareService.localFileTransferComplete(fileMeta.getFile(), seeder);
			
			if(!seeder) {
				IFileTransferCompleteMessage msgComplete = this.messageFactoryService.getFileTransferCompleteMessage();
				msgComplete.setPieFile(fileMeta.getFile());
				this.setDefaultAdresse(msgComplete);
				this.clusterManagementService.sendMessage(msgComplete);
			}
		} catch (Exception ex) {
			PieLogger.error(this.getClass(), "Exception in torrent task.", ex);
		}
	}

	@Override
	public void shutdown() {
		this.shutdown = true;
	}
	
}
