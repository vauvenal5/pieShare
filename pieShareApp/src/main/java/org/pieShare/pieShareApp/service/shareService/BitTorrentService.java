/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

import org.pieShare.pieShareApp.model.pieFile.FileMeta;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.task.localTasks.TorrentTask;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class BitTorrentService implements IBitTorrentService {

	private INetworkService networkService;
	private IBeanService beanService;
	private IExecutorService executorService;
	private IBase64Service base64Service;

	private Semaphore readPorts;
	private Semaphore writePorts;
	//for the time being it is neccessary to work with the FileMeta and not the PieFile due to the fact
	//that there can be multiple unrelated trackers present
	private ConcurrentHashMap<FileMeta, Integer> sharedFiles;

	public BitTorrentService() {
		this.sharedFiles = new ConcurrentHashMap<>();
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	/**
	 * If this FileMeta allready exists the value will be changed by the given
	 * value. If not it will be created with the given value. It returns true if
	 * the FileMeta was new and false otherwise.
	 *
	 * @param file
	 * @param value
	 * @return
	 */
	private boolean manipulateShareState(FileMeta file, Integer value) {
		synchronized (this.sharedFiles) {
			boolean isNew = true;

			PieLogger.trace(this.getClass(), "Manipulating share state for {} with HashCode {}.", file.getFile().getFileName(), file.hashCode());

			this.sharedFiles.keySet().stream().forEach(set -> {
				PieLogger.trace(this.getClass(), "List contains file meta {} with has code {}.", set.getFile().getFileName(), set.hashCode());
			});
			
			if (this.sharedFiles.containsKey(file)) {
				PieLogger.trace(this.getClass(), "Share state for {} exists.", file.getFile().getFileName());
				value = this.sharedFiles.get(file) + value;

				if (value <= 0) {
					this.sharedFiles.remove(file);
					return false;
				}

				isNew = false;
			}

			if (value >= 0) {
				this.sharedFiles.put(file, value);
			}

			return isNew;
		}
	}

	@Override
	public void initTorrentService() {
		//this section inits the semaphores
		int availablePorts = this.networkService.getNumberOfAvailablePorts(6881, 6889);
		if (availablePorts == 0) {
			//todo: handle this
			PieLogger.error(this.getClass(), "NO PORTS AVAILABLE ON THIS MACHINE!!!");
		}
		this.writePorts = new Semaphore(availablePorts);
		this.readPorts = new Semaphore((availablePorts / 2) - 1);
	}

	@Override
	public byte[] createMetaInformation(File localFile) throws CouldNotCreateMetaDataException {
		try {
			int port = -1;
			synchronized (this) {
				port = this.networkService.reserveAvailablePortStartingFrom(6969);
			}
			URI trackerUri = new URI("http://" + networkService.getLocalHost().getHostAddress() + ":" + String.valueOf(port) + "/announce");
			//todo: error handling when torrent null
			//todo: replace name by nodeName
			Torrent torrent = Torrent.create(localFile, trackerUri, "replaceThisByNodeName");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			torrent.save(baos);
			return this.base64Service.encode(baos.toByteArray());
		} catch (InterruptedException | IOException | URISyntaxException ex) {
			throw new CouldNotCreateMetaDataException(ex);
		}
	}

	@Override
	public void shareFile(FileMeta meta) {

		//this is important in case we are already sharing that file!
		if (!this.manipulateShareState(meta, 1)) {
			PieLogger.trace(this.getClass(), "Allready sharing file {}!", meta.getFile().getFileName());
			return;
		}

		try {
			this.writePorts.acquire();
			this.handleSharedTorrent(meta, true);
		} catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Acquire write failed!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Init torrent failed!", ex);
			this.torrentClientDone(true, meta);
		}
	}

	@Override
	public void clientDone(FileMeta meta) {
		this.manipulateShareState(meta, -1);
	}

	@Override
	public void handleFile(FileMeta meta) {
		//this is important in case we are already sharing that file!
		if (!this.manipulateShareState(meta, 1)) {
			PieLogger.trace(this.getClass(), "Allready handling file {}!", meta.getFile().getFileName());
			return;
		}

		try {
			this.readPorts.acquire();
			this.writePorts.acquire();
			this.handleSharedTorrent(meta, false);
		} catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Acquire read failed!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Init torrent failed!", ex);
			this.torrentClientDone(false, meta);
		}
	}

	private void handleSharedTorrent(FileMeta meta, boolean seeder) throws IOException {
		Torrent torrent = new Torrent(base64Service.decode(meta.getData()), seeder);
		TorrentTask task = this.beanService.getBean(PieShareAppBeanNames.getTorrentTask());
		task.setFile(meta);
		task.setTorrent(torrent);
		this.executorService.execute(task);
	}

	@Override
	public void torrentClientDone(boolean seeder, FileMeta file) {
		this.writePorts.release();
		if (!seeder) {
			this.readPorts.release();
		}

		synchronized (this) {
			this.sharedFiles.remove(file);
		}
	}

	@Override
	public boolean isShareActive(FileMeta file) {
		return (this.sharedFiles.getOrDefault(file, 0) > 0);
	}

	public boolean activeTorrents() {
		return !this.sharedFiles.isEmpty();
	}
}
