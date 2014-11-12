/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ComparerService implements IComparerService {

	private IFileService fileService;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public int compareWithLocalPieFile(PieFile pieFile) throws IOException, FileConflictException {

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());

		File localFile = new File(user.getPieShareConfiguration().getWorkingDir(), pieFile.getRelativeFilePath());

		if (!localFile.exists()) {
			PieLogger.debug(this.getClass(), "{} does not exist. Request this file.", pieFile.getRelativeFilePath());

			//todo: a history check has to be done here to check for deleted files
			return 1;
		}
		
		//todo-history: can be read from history instead
		PieFile localPieFile = this.fileService.getPieFile(localFile);

		return this.comparePieFiles(pieFile, localPieFile);
	}

	@Override
	public int compareWithHistory(PieFile pieFile) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int comparePieFiles(PieFile file1, PieFile file2) throws FileConflictException {
		PieLogger.debug(this.getClass(), "Comparing file: {} with file: {}", file1.getRelativeFilePath(), file2.getRelativeFilePath());
		//Remote File is older than local file
		//todo: should compare also file name!!!
		if (file1.getLastModified() == file2.getLastModified()) {
			if (Arrays.equals(file1.getMd5(), file2.getMd5())) {
				return 0;
			}
			//todo: fix this exception: why does it take a PieFile?!
			//throw new FileConflictException(String.format("Same Modification Date but different MD5 sum: %s", pieFile.getRelativeFilePath()), pieFile);
		} //Remote File is older than local file
		else if (file1.getLastModified() < file2.getLastModified()) {
			return -1;
		} //Remote File is newer than local file
		else if (file1.getLastModified() > file2.getLastModified()) {
			return 1;
		}

		throw new FileConflictException("Cannot handle this fils.", file1);
	}
}
