/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;
import org.pieShare.pieTools.pieUtilities.service.tempFolderService.api.ITempFolderService;

/**
 *
 * @author Svetoslav
 */
public class BitTorrentService implements IShareService, IShutdownableService {

	private Tracker tracker;
	private ITempFolderService tmpFolderService;
	private IClusterManagementService clusterManagementService;
	private IBeanService beanService;
	private IBase64Service base64Service;
	private INetworkService networkService;
	private IFileUtilsService fileUtilsService;
	private ConcurrentHashMap<PieFile, Integer> sharedFiles;
	private IShutdownService shutdownService;
	private boolean shutdown = false;
	private URI trackerUri;
	private Semaphore readPorts;
	private Semaphore writePorts;
	private IFileListenerService fileListener;
	private IPieShareConfiguration configuration;

	@PostConstruct
	public void init() {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		configuration = user.getPieShareConfiguration();
	}

	public void setShutdownService(IShutdownService shutdownService) {
		this.shutdownService = shutdownService;
	}

	public void setFileListener(IFileListenerService fileListener) {
		this.fileListener = fileListener;
	}

	public void setSharedFiles(ConcurrentHashMap<PieFile, Integer> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setTmpFolderService(ITempFolderService tmpFolderService) {
		this.tmpFolderService = tmpFolderService;
	}

	public void bitTorrentServicePost() {
		try {
			//todo: use beanService
			int port = this.networkService.getAvailablePortStartingFrom(6969);
			this.trackerUri = new URI("http://" + networkService.getLocalHost().getHostAddress() + ":" + String.valueOf(port) + "/announce");
			System.out.println(this.trackerUri.toString());
			InetSocketAddress ad = new InetSocketAddress(networkService.getLocalHost(), port);
			tracker = new Tracker(ad);
			//todo-sv: try to get local host out of cloud service

			int availablePorts = this.networkService.getNumberOfAvailablePorts(6881, 6889);
			if (availablePorts == 0) {
				//todo: handle this
				PieLogger.error(this.getClass(), "NO PORTS AVAILABLE ON THIS MACHINE!!!");
			}
			this.writePorts = new Semaphore((availablePorts / 2) - 1);
			this.readPorts = new Semaphore(availablePorts);

			this.sharedFiles = new ConcurrentHashMap<>();
			tracker.start();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		catch (URISyntaxException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}

		this.shutdownService.registerListener(this);
	}

	@Override
	public void shareFile(File file) {
		try {
			//todo: there is still some problem with the modification date!!! somewhere...
			//todo: ther is a bug when triing to share 0 byte files
			//todo: error handling when torrent null
			//todo: replace name by nodeName
			//URI uri = tracker.getAnnounceUrl().toURI();
			Torrent torrent = Torrent.create(file, this.trackerUri, "replaceThisByNodeName");

			//share torrent
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			torrent.save(baos);

			PieFile pieFile = this.fileUtilsService.getPieFile(file);
			this.initPieFileState(pieFile, 0);
			this.manipulatePieFileState(pieFile, 1);

			FileTransferMetaMessage metaMsg = new FileTransferMetaMessage();
			metaMsg.setMetaInfo(base64Service.encode(baos.toByteArray()));
			metaMsg.setPieFile(pieFile);
			//todo: security issues?
			TrackedTorrent tt = new TrackedTorrent(torrent);
			tracker.announce(tt);

			this.sendMetaMessageAndHandleSharedTorrent(pieFile, new SharedTorrent(torrent, file.getParentFile(), true), metaMsg);

			//todo: find out why ttorrent changes the date modified when sharing a file?!
			this.fileUtilsService.setCorrectModificationDate(pieFile);
		}
		catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}

	@Override
	public void handleFile(FileTransferMetaMessage msg) {

		try {
			this.initPieFileState(msg.getPieFile(), 0);

			File tmpDir = tmpFolderService.createTempFolder(msg.getPieFile().getFileName(), configuration.getTmpDir());
			SharedTorrent torrent = new SharedTorrent(base64Service.decode(msg.getMetaInfo()), tmpDir);

			handleSharedTorrent(msg.getPieFile(), torrent);

			if (this.shutdown) {
				return;
			}

			PieFile file = msg.getPieFile();

			//todo: move to file service
			File tmpFile = new File(tmpDir, msg.getPieFile().getFileName());
			File targetFile = new File(configuration.getWorkingDir(), msg.getPieFile().getRelativeFilePath());

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}

			Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			this.fileUtilsService.setCorrectModificationDate(file);

			//this.requestService.deleteRequestedFile(msg.getPieFile());
			FileUtils.deleteDirectory(tmpDir);

			FileTransferCompleteMessage msgComplete = new FileTransferCompleteMessage();
			msgComplete.setPieFile(msg.getPieFile());
			PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			this.clusterManagementService.sendMessage(msgComplete, user.getCloudName());

			//todo: start sharing
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		catch (Exception ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}

	private synchronized void initPieFileState(PieFile file, Integer count) {
		if (!this.sharedFiles.containsKey(file)) {
			this.sharedFiles.put(file, count);
		}
	}

	private synchronized void removePieFileState(PieFile file) {
		this.sharedFiles.remove(file);
	}

	private synchronized void manipulatePieFileState(PieFile file, Integer value) {
		if (this.sharedFiles.containsKey(file)) {
			this.sharedFiles.put(file, this.sharedFiles.get(file) + value);
		}
	}

	private void sendMetaMessageAndHandleSharedTorrent(PieFile pieFile, SharedTorrent torrent, FileTransferMetaMessage msg) {
		try {
			this.writePorts.acquire();
			//todo: think about some kind o PieAdress factory
			PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
			this.clusterManagementService.sendMessage(msg, user.getCloudName());
			handleSharedTorrent(pieFile, torrent);
			this.writePorts.release();
		}
		catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Sending Meta failed!", ex);
		}
		catch (InterruptedException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void handleSharedTorrent(PieFile pieFile, SharedTorrent torrent) {
		try {
			this.readPorts.acquire();

			this.fileListener.addPieFileToModifiedList(pieFile);
			//todo: handle ports out problem!!!
			//todo: this should run somehow over the beans
			Client client = new Client(networkService.getLocalHost(), torrent);

			//todo: this time has to move into the properties
			//todo: won't work for server and client the same way: problem with this timeout the server
			//shuts down after 30 seconds... implement other timeout strategy or rerequest messages
			//reregquest is maybe the better way
			client.share(30);

			/*if(torrent.isSeeder()) {
			 client.share();
			 }
			 else {
			 client.download();
			 }*/
			//client.waitForCompletion();
			while (!ClientState.DONE.equals(client.getState())) {

				if (this.shutdown) {
					client.stop();
					return;
				}

				// Check if there's an error
				if (ClientState.ERROR.equals(client.getState())) {
					client.stop();
					//todo: ports release when exception
					throw new Exception("ttorrent client Error State");
				}

				if (ClientState.SEEDING.equals(client.getState()) && this.sharedFiles.get(pieFile) <= 0) {
					client.stop();
				}

				// Display statistics
				PieLogger.debug(this.getClass(), "{} %% - state {} - {} bytes downloaded - {} bytes uploaded - {}", torrent.getCompletion(), client.getState(), torrent.getDownloaded(), torrent.getUploaded(), pieFile.getFileName());

				// Wait one second
				Thread.sleep(1000);
			}

			this.removePieFileState(pieFile);
			this.readPorts.release();
		}
		catch (IOException ex) {
			if (ex.getMessage().equals("No available port for the BitTorrent client!")) {
				//todo:handle all ports in use problem
				//todo: in this case the semaphores have to be recalculated
			}
			//todo: error handling?!
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		catch (Exception ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}

	//todo: there is no need to work dirctly with the message
	@Override
	public void fileTransferComplete(FileTransferCompleteMessage msg) {
		this.manipulatePieFileState(msg.getPieFile(), -1);
	}

	@Override
	public void handleActiveShare(PieFile pieFile) {
		this.manipulatePieFileState(pieFile, 1);
	}

	@Override
	public void shutdown() {
		this.tracker.stop();
		this.shutdown = true;
	}
}
