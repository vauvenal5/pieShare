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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.replicatedHashMapService.IReplicatedHashMap;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.fileUtileService.api.IFileUtileService;
import org.pieShare.pieTools.pieUtilities.service.tempFolderService.api.ITempFolderService;

/**
 *
 * @author Svetoslav
 */
public class BitTorrentService implements IShareService {

	private Tracker tracker;
	private IPieShareAppConfiguration configurationService;
	private ITempFolderService tmpFolderService;
	private IFileUtileService fileUtileService;
	private IClusterManagementService clusterManagementService;
	private IBeanService beanService;
	private IBase64Service base64Service;
	private INetworkService networkService;
	private IFileService fileService;
	private IReplicatedHashMap<PieFile, List<URI>> mapService;
	private ConcurrentHashMap<PieFile, Integer> sharedFiles;
	private IRequestService requestService;

	public void setSharedFiles(ConcurrentHashMap<PieFile, Integer> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
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

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setConfigurationService(IPieShareAppConfiguration configurationService) {
		this.configurationService = configurationService;
	}

	public void setTmpFolderService(ITempFolderService tmpFolderService) {
		this.tmpFolderService = tmpFolderService;
	}

	public void setFileUtileService(IFileUtileService fileUtileService) {
		this.fileUtileService = fileUtileService;
	}

	@PostConstruct
	private void BitTorrentServicePost() {
		try {
			//todo: use beanService
			tracker = new Tracker(new InetSocketAddress(networkService.getLocalHost(), 6969));
			this.sharedFiles = new ConcurrentHashMap<>();
			tracker.start();
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void shareFile(File file) {
		try {
			//todo: think about some kind o PieAdress factory
			PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
			IClusterService clusterService = this.clusterManagementService.connect(user.getCloudName());
			//todo: error handling when torrent null
			//todo: replace name by nodeName
			//URI uri = tracker.getAnnounceUrl().toURI();
			URI uri = new URI("http://"+networkService.getLocalHost().getHostAddress()+":6969/announce");
			Torrent torrent = Torrent.create(file, uri, "replaceThisByNodeName");

			//share torrent
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			torrent.save(baos);

			PieFile pieFile = fileService.genPieFile(file);
			this.initPieFileState(pieFile, 0);
			this.manipulatePieFileState(pieFile, 1);

			FileTransferMetaMessage metaMsg = new FileTransferMetaMessage();
			metaMsg.setMetaInfo(base64Service.encode(baos.toByteArray()));
			metaMsg.setPieFile(pieFile);
			//todo: security issues?
			TrackedTorrent tt = new TrackedTorrent(torrent);
			tracker.announce(tt);
			
			clusterService.sendMessage(metaMsg);
			handleSharedTorrent(pieFile, new SharedTorrent(torrent, file.getParentFile(), true));
			/*long modD = file.lastModified();
			Client seeder = new Client(networkService.getLocalHost(), new SharedTorrent(torrent, file.getParentFile(), true));
			if (!file.setLastModified(modD)) {
				System.out.println("Torrent modified lastModificationDate");
			}
			seeder.share();
			seeder.*/
		} catch (InterruptedException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (URISyntaxException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClusterManagmentServiceException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClusterServiceException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void handleFile(FileTransferMetaMessage msg) {

		try {		
			this.initPieFileState(msg.getPieFile(), 0);
			
			File tmpDir = tmpFolderService.createTempFolder(msg.getPieFile().getFileName(), configurationService.getTempCopyDirectory());
			SharedTorrent torrent = new SharedTorrent(base64Service.decode(msg.getMetaInfo()), tmpDir);
			
			handleSharedTorrent(msg.getPieFile(), torrent);
			/*Client client = new Client(networkService.getLocalHost(), torrent);
			client.share();
			
			while (!ClientState.DONE.equals(client.getState())) {
				// Check if there's an error
				if (ClientState.ERROR.equals(client.getState())) {
					throw new Exception("ttorrent client Error State");
				}
				
				if(ClientState.SEEDING.equals(client.getState())) {
					System.out.println("SEEDING STATE!!");
				}
				
				if(ClientState.SHARING.equals(client.getState())) {
					System.out.println("SHARIN STATE!!");
				}
				
				Thread.sleep(1000);
			}*/
			
			File tmpFile = new File(tmpDir, msg.getPieFile().getFileName());
			File targetFile = new File(configurationService.getWorkingDirectory(), msg.getPieFile().getRelativeFilePath());

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}

			Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			if (!targetFile.setLastModified(msg.getPieFile().getLastModified())) {
				System.out.println("WARNING: Could not set LastModificationDate");
			}
			
			this.requestService.deleteRequestedFile(msg.getPieFile());
			fileUtileService.deleteRecursive(tmpDir);
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private synchronized void initPieFileState(PieFile file, Integer count) {
		if(!this.sharedFiles.containsKey(file)) {
			this.sharedFiles.put(file, count);
		}
	}
	
	private synchronized void removePieFileState(PieFile file) {
		this.sharedFiles.remove(file);
	}
	
	private synchronized void manipulatePieFileState(PieFile file, Integer value) {
		if(!this.sharedFiles.containsKey(file)) {
			this.sharedFiles.put(file, this.sharedFiles.get(file)+value);
		}
	}
	
	private void handleSharedTorrent(PieFile pieFile, SharedTorrent torrent) {
		try {
			Client client = new Client(networkService.getLocalHost(), torrent);
			client.share();
			
			//client.waitForCompletion();
			while (!ClientState.DONE.equals(client.getState())) {
				// Check if there's an error
				if (ClientState.ERROR.equals(client.getState())) {
					throw new Exception("ttorrent client Error State");
				}
				
				if(ClientState.SEEDING.equals(client.getState())) {
					FileTransferCompleteMessage msg = new FileTransferCompleteMessage();
					msg.setPieFile(pieFile);
					PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
					
					this.clusterManagementService.sendMessage(msg, user.getCloudName());
				}
				
				if(ClientState.SEEDING.equals(client.getState()) && this.sharedFiles.get(pieFile) <= 0) {
					this.removePieFileState(pieFile);
					client.stop();
				}
				
				// Display statistics
				System.out.printf("%f %% - %d bytes downloaded - %d bytes uploaded\n", torrent.getCompletion(), torrent.getDownloaded(), torrent.getUploaded());

				// Wait one second
				Thread.sleep(1000);
			}
			
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void fileTransferComplete(FileTransferCompleteMessage msg) {
		this.manipulatePieFileState(msg.getPieFile(), -1);
	}

	@Override
	public void handleActiveShare(PieFile pieFile) {
		this.manipulatePieFileState(pieFile, 1);
	}
}
