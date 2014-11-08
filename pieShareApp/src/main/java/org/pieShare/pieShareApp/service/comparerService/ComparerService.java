/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ComparerService implements IComparerService {

	private IRequestService requestService;
	private IFileUtilsService fileUtilsService;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	@Override
	public boolean isPieFileDesired(PieFile remotePieFile) throws IOException, FileConflictException {

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		
		PieLogger.debug(this.getClass(), "Comparing file: {}", remotePieFile.getRelativeFilePath());

		File localFile = new File(user.getPieShareConfiguration().getWorkingDir(), remotePieFile.getRelativeFilePath());

		if (!localFile.exists()) {
			PieLogger.debug(this.getClass(), "{} does not exist. Request this file.", remotePieFile.getRelativeFilePath());
			return true;
		}

		PieFile localPieFile = this.fileUtilsService.getPieFile(localFile);

		//Remote File is older than local file
		//todo: should compare also file name!!!
		if (remotePieFile.getLastModified() == localFile.lastModified()) {
			if (Arrays.equals(remotePieFile.getMd5(), localPieFile.getMd5())) {
				PieLogger.debug(this.getClass(), "{} is already there. Do not request.", remotePieFile.getRelativeFilePath());
				return false;
			}
			//todo: fix this exception: why does it take a PieFile?!
			throw new FileConflictException(String.format("Same Modification Date but different MD5 sum: %s", remotePieFile.getRelativeFilePath()), remotePieFile);
		} //Remote File is older than local file
		else if (remotePieFile.getLastModified() < localFile.lastModified()) {
			throw new FileConflictException(String.format("Different Modification Datej: %s", remotePieFile.getRelativeFilePath()), remotePieFile);
		} //Remote File is newer than local file
		else if (remotePieFile.getLastModified() > localFile.lastModified()) {
			return true;
		}

		throw new FileConflictException("Cannot handle this file.", remotePieFile);
	}

	@Override
	public void comparePieFileList(List<PieFile> list) throws IOException, FileConflictException {

		for (PieFile pieFile : list) {
			comparePieFile(pieFile);
		}
	}

	@Override
	public void comparePieFile(PieFile pieFile) throws IOException, FileConflictException {

		//todo: the requestService could do the check of the requestedFileList internally!?!
		if (isPieFileDesired(pieFile)) {
			requestService.requestFile(pieFile);
		}

	}

}
