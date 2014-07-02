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

			FileTransferMetaMessage metaMsg = new FileTransferMetaMessage();
			metaMsg.setMetaInfo(base64Service.encode(baos.toByteArray()));
			metaMsg.setPieFile(pieFile);
			//todo: security issues?
			TrackedTorrent tt = new TrackedTorrent(torrent);
			tracker.announce(tt);
			
			handleSharedTorrent(new SharedTorrent(torrent, file.getParentFile(), true));
			/*long modD = file.lastModified();
			Client seeder = new Client(networkService.getLocalHost(), new SharedTorrent(torrent, file.getParentFile(), true));
			if (!file.setLastModified(modD)) {
				System.out.println("Torrent modified lastModificationDate");
			}
			seeder.share();
			seeder.*/

			clusterService.sendMessage(metaMsg);
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
			
		//will be deleted
		if (!fileService.checkMergeFile(msg.getPieFile())) {
			return;
		}

		try {
			File tmpDir = tmpFolderService.createTempFolder(msg.getPieFile().getFileName(), configurationService.getTempCopyDirectory());

			SharedTorrent torrent = new SharedTorrent(base64Service.decode(msg.getMetaInfo()), tmpDir);
			Client client = new Client(networkService.getLocalHost(), torrent);

			//seed for 10min to other cluster members
			//todo: move this to settings
			client.download();
			

			//client.waitForCompletion();
			while (!ClientState.DONE.equals(client.getState())) {
				// Check if there's an error
				if (ClientState.ERROR.equals(client.getState())) {
					throw new Exception("ttorrent client Error State");
				}

				// Display statistics
				System.out.printf("%f %% - %d bytes downloaded - %d bytes uploaded\n", torrent.getCompletion(), torrent.getDownloaded(), torrent.getUploaded());

				// Wait one second
				TimeUnit.SECONDS.sleep(1);
			}

			client.stop();
			File tmpFile = new File(tmpDir, msg.getPieFile().getFileName());
			File targetFile = new File(configurationService.getWorkingDirectory(), msg.getPieFile().getRelativeFilePath());

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}

			Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			if (!targetFile.setLastModified(msg.getPieFile().getLastModified())) {
				System.out.println("WARNING: Could not set LastModificationDate");
			}

			fileUtileService.deleteRecursive(tmpDir);
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void handleSharedTorrent(SharedTorrent torrent) {
		try {
			Client client = new Client(networkService.getLocalHost(), torrent);
			client.share();
			
			//added timertask for stoping client after work done
			
			//client.waitForCompletion();
			while (!ClientState.DONE.equals(client.getState())) {
				// Check if there's an error
				if (ClientState.ERROR.equals(client.getState())) {
					throw new Exception("ttorrent client Error State");
				}
				
				// Display statistics
				System.out.printf("%f %% - %d bytes downloaded - %d bytes uploaded\n", torrent.getCompletion(), torrent.getDownloaded(), torrent.getUploaded());

				// Wait one second
				Thread.sleep(500);
			}
			
			client.stop();
		} catch (IOException ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void fileTransferComplete(FileTransferCompleteMessage msg) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
