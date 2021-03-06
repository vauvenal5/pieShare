/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

import org.pieShare.pieShareApp.model.pieFile.FileMeta;
import java.io.File;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IBitTorrentService {
	void initTorrentService();
	
	void clientDone(FileMeta meta);
	void torrentClientDone(boolean seeder, FileMeta file);
	void shareFile(FileMeta file);
	void handleFile(FileMeta file);
	boolean isShareActive(FileMeta file);
	byte[] createMetaInformation(File localFile) throws CouldNotCreateMetaDataException;
}
