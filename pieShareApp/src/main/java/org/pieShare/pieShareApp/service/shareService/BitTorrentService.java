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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;

/**
 *
 * @author Svetoslav
 */
public class BitTorrentService implements IShareService {
    
    Tracker tracker;
    IPieShareAppConfiguration configurationService;
    
    @Override
    public void shareFile(File file) {
        try {
            tracker = new Tracker(new InetSocketAddress(6969));
            //todo: replace name by nodeName
            Torrent torrent = Torrent.create(file, tracker.getAnnounceUrl().toURI(), "replaceThisByNodeName");
            
            //share torrent
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            torrent.save(baos);
            FileTransferMetaMessage metaMsg = new FileTransferMetaMessage();
            metaMsg.setMetaInfo(baos.toByteArray());
            //todo: send the message
            
            tracker.announce(new TrackedTorrent(torrent));
        } catch (InterruptedException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void handleFile(FileTransferMetaMessage msg) {
        try {
            //todo: set working directory
            SharedTorrent torrent = new SharedTorrent(msg.getMetaInfo(), configurationService.getTempCopyDirectory());
            Client client = new Client(InetAddress.getLocalHost(), torrent);
            //seed for 10min to other cluster members
            //todo: move this to settings
            client.share(600);
        } catch (IOException ex) {
            Logger.getLogger(BitTorrentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
