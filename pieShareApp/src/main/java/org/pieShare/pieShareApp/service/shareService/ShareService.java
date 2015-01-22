/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

import com.turn.ttorrent.client.SharedTorrent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.api.IFileTransferMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.fileEncryptionService.IFileEncryptionService;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.tempFolderService.api.ITempFolderService;

/**
 *
 * @author Svetoslav
 */
public class ShareService implements IShareService{
	
	private IBitTorrentService bitTorrentService;
	private ILocalFileCompareService comparerService;
	
	private ITempFolderService tmpFolderService;
	private IClusterManagementService clusterManagementService;
	private IBeanService beanService;
	private IBase64Service base64Service;
	private IFileService fileService;
	private ConcurrentHashMap<PieFile, Integer> sharedFiles;
	
	
	private IPieShareConfiguration configuration;
	
	
	private IFileEncryptionService fileEncryptionService;
	private IMessageFactoryService messageFactoryService;
	private IFileWatcherService fileWatcherService;

	public void init() {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		configuration = user.getPieShareConfiguration();
		this.sharedFiles = new ConcurrentHashMap<>();
	}

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}

	public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
		this.messageFactoryService = messageFactoryService;
	}

	public void setFileEncryptionService(IFileEncryptionService fileEncryptionService) {
		this.fileEncryptionService = fileEncryptionService;
	}

	public void setFileWatcherService(IFileWatcherService fileWatcherService) {
		this.fileWatcherService = fileWatcherService;
	}

	public void setSharedFiles(ConcurrentHashMap<PieFile, Integer> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFileUtilsService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setTmpFolderService(ITempFolderService tmpFolderService) {
		this.tmpFolderService = tmpFolderService;
	}
	
	@Override
	public File prepareFile(PieFile file) throws NoLocalFileException
	{		
		//PieFile tmpFile = this.fileService.getTmpPieFile(file);
		File localFile = this.fileService.getAbsolutePath(file).toFile();
		//File localTmpFile = this.fileService.getAbsoluteTmpPath(tmpFile).toFile();
		File localTmpFileParent = this.fileService.getAbsoluteTmpPath(file).toFile().getParentFile();
		File localTmpFile = new File(localTmpFileParent, file.getFileName()+".enc");
		
		this.comparerService.compareWithLocalPieFile(file)
		
		if(!localFile.exists()) {
			throw new NoLocalFileException("Local file doesn't exist!");
		}
		
		if(this.isPrepared(file) && localTmpFile.exists()) {			
			return localTmpFile;
		}
		
		//TODO: create dirs???!!!
		this.fileEncryptionService.encryptFile(localFile, localTmpFile);
		this.initCheckPieFileState(file, 0);
		
		return localTmpFile;
	}

	/*@Override
	public void shareFile(PieFile file) 
	{
		try {
			//PieFile tmpFile = this.fileService.getTmpPieFile(file);
			File localFile = this.fileService.getAbsolutePath(file).toFile();
			//File localTmpFile = this.fileService.getAbsoluteTmpPath(tmpFile).toFile();
			File localTmpFileParent = this.fileService.getAbsoluteTmpPath(file).toFile().getParentFile();
			File localTmpFile = new File(localTmpFileParent, file.getFileName()+".enc");
			
			
			this.fileEncryptionService.encryptFile(localFile, localTmpFile);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//this.bitTorrentService.shareTorrent(tmpFile, baos);
			this.bitTorrentService.shareTorrent(file, localTmpFile, baos);
			
			this.initCheckPieFileState(file, 0);
			this.manipulatePieFileState(file, 1);

			IFileTransferMetaMessage metaMsg = this.messageFactoryService.getFileTransferMetaMessage();
			metaMsg.setMetaInfo(base64Service.encode(baos.toByteArray()));
			metaMsg.setPieFile(file);
			//todo: think about some kind o PieAdress factory
			PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
			metaMsg.getAddress().setChannelId(user.getUserName());
			metaMsg.getAddress().setClusterName(user.getCloudName());
			this.clusterManagementService.sendMessage(metaMsg);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}*/
	
	@Override
	public File handleFile(PieFile file) throws AllreadyInitializedException {
		if(this.initCheckPieFileState(file, 1)) {
			File localTmpFile = this.fileService.getAbsoluteTmpPath(file).toFile();
			//todo: does this belong into the fileService?
			if (!localTmpFile.getParentFile().exists()) {
				localTmpFile.getParentFile().mkdirs();
			}
			
			return localTmpFile.getParentFile();
		}
		
		throw new AllreadyInitializedException();
	}

	//todo: maybe merge this with handle activeshare
	/*@Override
	public void handleFile(PieFile file, byte[] metaInfo) {
		
		if(this.sharedFiles.containsKey(file)) {
			//allready handling this file
			return;
		}

		try {
			this.initCheckPieFileState(file, 0);

			//File tmpDir = tmpFolderService.createTempFolder(file.getFileName(), configuration.getTmpDir());
			File localTmpFile = this.fileService.getAbsoluteTmpPath(file).toFile();
			//todo: does this belong into the fileService?
			if (!localTmpFile.getParentFile().exists()) {
				localTmpFile.getParentFile().mkdirs();
			}
			//SharedTorrent torrent = new SharedTorrent(base64Service.decode(metaInfo), tmpDir);
			SharedTorrent torrent = new SharedTorrent(base64Service.decode(metaInfo), localTmpFile.getParentFile());
			
			this.bitTorrentService.handleSharedTorrent(file, torrent);
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
		catch (Exception ex) {
			PieLogger.error(this.getClass(), "Sharing error.", ex);
		}
	}*/
	
	

	private synchronized void removePieFileState(PieFile file) {
		this.sharedFiles.remove(file);
	}

	private synchronized void manipulatePieFileState(PieFile file, Integer value) {
		if (this.sharedFiles.containsKey(file)) {
			int newValue = this.sharedFiles.get(file) + value;
			
			if(newValue <= 0) {
				this.removePieFileState(file);
				return;
			}
			
			this.sharedFiles.put(file, newValue);
		}
	}

	@Override
	public void localFileTransferComplete(PieFile file, boolean source) {
		try {
			File localTmpFile = this.fileService.getAbsoluteTmpPath(file).toFile();
			File localTmpFileParent = this.fileService.getAbsoluteTmpPath(file).toFile().getParentFile();
			File localEncTmpFile = new File(localTmpFileParent, file.getFileName()+".enc");
		
			if(!source) {
				File localFile = this.fileService.getAbsolutePath(file).toFile();
				
				//todo: does this belong into the fileService?
				if (!localFile.getParentFile().exists()) {
					localFile.getParentFile().mkdirs();
				}
				
				this.fileEncryptionService.decryptFile(localEncTmpFile, localTmpFile);
				
				this.fileWatcherService.addPieFileToModifiedList(file);
				Files.move(localTmpFile.toPath(), localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				//this.fileWatcherService.addPieFileToModifiedList(file);
				//FileUtils.moveFile(localTmpFile, localFile);
				//todo: this i wrong here!
				//todo: has to move to the according task
				this.fileWatcherService.removePieFileFromModifiedList(file);
				
				this.fileWatcherService.addPieFileToModifiedList(file);
				this.fileService.setCorrectModificationDate(file);
				
				//todo: is it better to delete the enc file or not?
			}
		
			//localTmpFile.delete();
		
			this.manipulatePieFileState(file, -1);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error!", ex);
		}
	}
	
	@Override
	public void remoteFileTransferComplete(PieFile file) {
		this.manipulatePieFileState(file, -1);
	}

	@Override
	public void handleRemoteRequestForActiveShare(PieFile pieFile) {
		try {
			PieFile tmpFile = this.fileService.getTmpPieFile(pieFile);
			this.manipulatePieFileState(tmpFile, 1);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error!", ex);
		}
	}
	
	private synchronized boolean initCheckPieFileState(PieFile file, int count) {		
		if(!this.isShareActive(file)) {
			this.sharedFiles.put(file, count);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isShareActive(PieFile file) {
		if(!this.isPrepared(file)) {
			return false;
		}
		
		return (this.sharedFiles.get(file) > 0);
	}
	
	@Override
	public synchronized boolean isPrepared(PieFile file) {
		return this.sharedFiles.containsKey(file);
	}
}
