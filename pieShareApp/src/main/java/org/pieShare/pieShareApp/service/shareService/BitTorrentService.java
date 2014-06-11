/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
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
    private IClusterService clusterService;
    private IFileUtileService fileUtileService;

    public void setConfigurationService(IPieShareAppConfiguration configurationService) {
        this.configurationService = configurationService;
    }

    public void setTmpFolderService(ITempFolderService tmpFolderService) {
        this.tmpFolderService = tmpFolderService;
    }

    public void setClusterService(IClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public void setFileUtileService(IFileUtileService fileUtileService) {
        this.fileUtileService = fileUtileService;
    }
    
    @PostConstruct
    private void BitTorrentServicePost() {
        try {
            //todo: use beanService
            tracker = new Tracker(new InetSocketAddress(6969));
        } catch (IOException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void shareFile(PieFile file) {
        try {
            //todo: error handling when torrent null
            //todo: replace name by nodeName
            Torrent torrent = Torrent.create(file.getFile(), tracker.getAnnounceUrl().toURI(), "replaceThisByNodeName");
            
            //share torrent
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            torrent.save(baos);
           
            FileTransferMetaMessage metaMsg = new FileTransferMetaMessage();
            metaMsg.setMetaInfo(baos.toByteArray());
            metaMsg.setFilename(file.getFileName());
            metaMsg.setRelativePath(file.getRelativeFilePath());
            //todo: security issues?
            tracker.announce(new TrackedTorrent(torrent));
            
            this.clusterService.sendMessage(metaMsg);
        } catch (InterruptedException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClusterServiceException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void handleFile(FileTransferMetaMessage msg) {
        try {
            File tmpDir = tmpFolderService.createTempFolder(msg.getFilename(), configurationService.getTempCopyDirectory());

            SharedTorrent torrent = new SharedTorrent(msg.getMetaInfo(), tmpDir);
            Client client = new Client(InetAddress.getLocalHost(), torrent);

            //seed for 10min to other cluster members
            //todo: move this to settings
            client.share(600);
            
            client.waitForCompletion();
            
            File tmpFile = new File(tmpDir, msg.getFilename());
            File targetDir = new File(configurationService.getWorkingDirectory(), msg.getRelativePath());
            
            Files.copy(tmpFile.toPath(), targetDir.toPath());
            
            fileUtileService.deleteRecursive(tmpDir);
        } catch (IOException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
