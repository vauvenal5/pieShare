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
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ComparerService implements IComparerService {

	private IPieShareAppConfiguration pieAppConfig;
	private IRequestService requestService;
        private IFileUtilsService fileUtilsService;

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

        public void setFileUtilsService(IFileUtilsService fileUtilsService) {
            this.fileUtilsService = fileUtilsService;
        }

	public void setPieShareConfiguration(IPieShareAppConfiguration pieAppConfig) {
		this.pieAppConfig = pieAppConfig;
	}

	@Override
	public boolean isPieFileDesired(PieFile remotePieFile) throws IOException, FileConflictException {

		PieLogger.debug(this.getClass(), "Comparing file: {}", remotePieFile.getRelativeFilePath());

		File localFile = new File(pieAppConfig.getWorkingDirectory(), remotePieFile.getRelativeFilePath());
		
		if (!localFile.exists()) {
			PieLogger.debug(this.getClass(), "{} does not exist. Request this file.", remotePieFile.getRelativeFilePath());
			return true;
		}
		
		PieFile localPieFile = this.fileUtilsService.getPieFile(localFile);


		//Remote File is older than local file
		if (remotePieFile.getLastModified() == localFile.lastModified()) {
			if (Arrays.equals(remotePieFile.getMd5(), localPieFile.getMd5())) {
				PieLogger.debug(this.getClass(), "{} is already there. Do not request.", remotePieFile.getRelativeFilePath());
				return false;
			}
			PieLogger.debug(this.getClass(), "{} is already there. Do not request.", remotePieFile.getRelativeFilePath());
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
