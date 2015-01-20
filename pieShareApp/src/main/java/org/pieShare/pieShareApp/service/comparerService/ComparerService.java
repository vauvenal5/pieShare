/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ComparerService implements ICompareService {

	@Override
	public int comparePieFiles(PieFile remoteFile, PieFile localFile) throws FileConflictException {
		PieLogger.debug(this.getClass(), "Comparing file: {} with file: {}", remoteFile.getRelativeFilePath(), localFile.getRelativeFilePath());
		//Remote File is older than local file
		//todo: should compare also file name!!!
		if (remoteFile.getLastModified() == localFile.getLastModified()) {
			if (Arrays.equals(remoteFile.getMd5(), localFile.getMd5())) {
				return 0;
			}
			//todo: fix this exception: why does it take a PieFile?!
			//throw new FileConflictException(String.format("Same Modification Date but different MD5 sum: %s", pieFile.getRelativeFilePath()), pieFile);
		} //Remote File is older than local file
		else if (remoteFile.getLastModified() < localFile.getLastModified()) {
			return -1;
		} //Remote File is newer than local file
		else if (remoteFile.getLastModified() > localFile.getLastModified()) {
			return 1;
		}

		throw new FileConflictException("Cannot handle this fils.", remoteFile);
	}
}
