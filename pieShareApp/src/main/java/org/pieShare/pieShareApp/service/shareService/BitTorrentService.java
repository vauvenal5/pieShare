/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.task.localTasks.TorrentTask;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class BitTorrentService implements IBitTorrentService, IShutdownableService {
	
	private INetworkService networkService;
	private IBeanService beanService;
	private IExecutorService executorService;
	private IBase64Service base64Service;
	private IShareService shareService;
	
	private Tracker tracker;
	private URI trackerUri;
	private Semaphore readPorts;
	private Semaphore writePorts;
	private boolean shutdown;

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void initTorrentService() {
			this.shutdown = false;
			//this section inits the semaphores
			int availablePorts = this.networkService.getNumberOfAvailablePorts(6881, 6889);
			if (availablePorts == 0) {
				//todo: handle this
				PieLogger.error(this.getClass(), "NO PORTS AVAILABLE ON THIS MACHINE!!!");
			}
			this.writePorts = new Semaphore((availablePorts / 2) - 1);
			this.readPorts = new Semaphore(availablePorts);			
	}

	@Override
	public byte[] anounceTorrent(File localFile) {
		if(this.shutdown) {
			return null;
		}
		
		try {
			if(tracker == null) {
				//this section inits the local tracker
				//todo: use beanService
				int port = this.networkService.getAvailablePortStartingFrom(6969);
				this.trackerUri = new URI("http://" + networkService.getLocalHost().getHostAddress() + ":" + String.valueOf(port) + "/announce");
				PieLogger.info(this.getClass(), this.trackerUri.toString());
				InetSocketAddress ad = new InetSocketAddress(networkService.getLocalHost(), port);
				this.tracker = new Tracker(ad);
				tracker.start();
			}

			//todo: ther is a bug when triing to share 0 byte files
			//todo: error handling when torrent null
			//todo: replace name by nodeName
			Torrent torrent = Torrent.create(localFile, this.trackerUri, "replaceThisByNodeName");
			//todo: security issues?
			TrackedTorrent tt = new TrackedTorrent(torrent);
			tracker.announce(tt);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			torrent.save(baos);
			return this.base64Service.encode(baos.toByteArray());
		} catch (InterruptedException | URISyntaxException | IOException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		
		return null;
	}
	
	@Override
	public void shareTorrent(PieFile file, File localFile, OutputStream out) {
		try {
			//File localFile = this.fileService.getAbsolutePath(file).toFile();
			//Torrent torrent = Torrent.create(localFile, this.trackerUri, "replaceThisByNodeName");
			Torrent torrent = this.anounceTorrent(localFile);
			if(torrent == null) {
				//todo: fail handling has to be added
				return;
			}
			SharedTorrent sharredTorrent = new SharedTorrent(torrent, localFile.getParentFile(), true);
			
			
			this.writePorts.acquire();
			this.handleSharedTorrent(file, sharredTorrent);
			
			torrent.save(out);
		}
		catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Sending Meta failed!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Sending Meta failed!", ex);
		}
	}
	
	@Override
	public void handleShareTorrent(PieFile pieFile, byte[] meta, File destDir) {
		try {
			SharedTorrent torrent = new SharedTorrent(base64Service.decode(meta), destDir);
			this.handleSharedTorrent(pieFile, torrent);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Couldn't convert torrent file.", ex);
		}
	}
	
	private void handleSharedTorrent(PieFile pieFile, SharedTorrent torrent) {
		try {
			this.readPorts.acquire();
			TorrentTask task = this.beanService.getBean(PieShareAppBeanNames.getTorrentTask());
			task.setFile(pieFile);
			task.setTorrent(torrent);
			if(this.shutdown) {
				return;
			}
			
			this.executorService.execute(task);
		} catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}

	@Override
	public void torrentClientDone(boolean seeder) {
		this.readPorts.release();
		if(seeder) {
			this.writePorts.release();
		}
	}
	
	@Override
	public void shutdown() {
		
		if(this.tracker != null) {
			this.tracker.stop();
		}
		
		this.shutdown = true;
	}
}
