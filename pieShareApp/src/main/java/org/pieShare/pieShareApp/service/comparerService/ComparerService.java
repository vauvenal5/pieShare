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
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ComparerService implements IComparerService {

	private IPieShareAppConfiguration pieAppConfig;
	private IFileService fileService;
	private IRequestService requestService;

	private final PieLogger logger = new PieLogger(ComparerService.class);

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setPieShareConfiguration(IPieShareAppConfiguration pieAppConfig) {
		this.pieAppConfig = pieAppConfig;
	}

	@Override
	public boolean isPieFileDesired(PieFile remotePieFile) throws IOException, FileConflictException {

		logger.debug("Comparing file: " + remotePieFile.getRelativeFilePath());

		File localFile = new File(pieAppConfig.getWorkingDirectory(), remotePieFile.getRelativeFilePath());
		PieFile localPieFile = fileService.genPieFile(localFile);

		if (!localFile.exists()) {
			logger.debug(remotePieFile.getRelativeFilePath() + " does not exist. Request this file.");
			return true;
		}

		//Remote File is older than local file
		if (remotePieFile.getLastModified() == localFile.lastModified()) {
			if (Arrays.equals(remotePieFile.getMd5(), localPieFile.getMd5())) {
				logger.debug(remotePieFile.getRelativeFilePath() + " is already there. Do not request.");
				return false;
			}
			logger.debug(remotePieFile.getRelativeFilePath() + " is already there. Do not request.");
			throw new FileConflictException("Same Modification Date but different MD5 sum.", remotePieFile);
		} //Remote File is older than local file
		else if (remotePieFile.getLastModified() < localFile.lastModified()) {
			throw new FileConflictException("Same Modification Date but different MD5 sum.", remotePieFile);
		} //Remote File is newer than local file
		else if (remotePieFile.getLastModified() > localFile.lastModified()) {
			return true;
		}

		throw new FileConflictException("Cannot handle this file.", remotePieFile);
	}

	@Override
	public void comparePieFileList(List<PieFile> list) throws IOException, FileConflictException {

		for (PieFile pieFile : list) {
			if (!requestService.getRequestedFileList().contains(pieFile) && isPieFileDesired(pieFile)) {
				requestService.requestFile(pieFile);
			}
		}
	}

	@Override
	public void comparePieFile(PieFile pieFile) throws IOException, FileConflictException {

		if (!requestService.getRequestedFileList().contains(pieFile) && isPieFileDesired(pieFile)) {
			requestService.requestFile(pieFile);
		}

	}

}
