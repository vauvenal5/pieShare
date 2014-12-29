/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import java.io.File;
import java.io.OutputStream;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IBitTorrentService {
	void initTorrentService();
	void anounceTorrent(Torrent torrent);
	
	void torrentClientDone(boolean seeder);
	void shareTorrent(PieFile file, File localFile, OutputStream out);
	void handleSharedTorrent(PieFile pieFile, SharedTorrent torrent);
}
