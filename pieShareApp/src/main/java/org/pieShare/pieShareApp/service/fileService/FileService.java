package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.task.FileListRequestTask;
import org.pieShare.pieShareApp.task.FileListTask;
import org.pieShare.pieShareApp.task.FileMetaTask;
import org.pieShare.pieShareApp.task.FileRequestTask;
import org.pieShare.pieShareApp.task.FileTransferCompleteTask;
import org.pieShare.pieShareApp.task.NewFileTask;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 * @author richy
 */
public class FileService implements IFileService, IClusterAddedListener {

	private IExecutorService executorService = null;
	private IFileWatcherService fileWatcher;
	private IPieShareAppConfiguration pieAppConfig;
	private IBeanService beanService;
	private IShareService shareService;
	private IHashService hashService;
	private IRequestService requestService;
	private IClusterManagementService clusterManagementService;
        private IFileUtilsService fileUtilsService;

	public FileService() {

	}

        public void setFileUtilsService(IFileUtilsService fileUtilsService) {
            this.fileUtilsService = fileUtilsService;
        }

	public void setClusterManagementService(IClusterManagementService clusterManagementService)
	{
		this.clusterManagementService = clusterManagementService;
	}
	
	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration) {
		this.pieAppConfig = pieShareAppConfiguration;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setMd5Service(IHashService hashService) {
		this.hashService = hashService;
	}

	public void initFileService() {
		/*  try
		 {
		 registerAll(pieAppConfig.getWorkingDirectory());

		 }
		 catch (IOException ex)
		 {
		 logger.error("Error parsing workingDir at startup");
		 }
		 */
		addWatchDirectory(pieAppConfig.getWorkingDirectory());
		
		this.clusterManagementService.getClusterAddedEventBase().addEventListener(this);
	}

	public void setFileWatcher(IFileWatcherService fileWatcher) {
		this.fileWatcher = fileWatcher;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
		this.executorService.registerTask(FileTransferMetaMessage.class, FileMetaTask.class);
		this.executorService.registerTask(FileRequestMessage.class, FileRequestTask.class);
		this.executorService.registerTask(NewFileMessage.class, NewFileTask.class);
		this.executorService.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);
		this.executorService.registerTask(FileListRequestMessage.class, FileListRequestTask.class);
		this.executorService.registerTask(FileListMessage.class, FileListTask.class);
	}

	private void addWatchDirectory(File file) {
		fileWatcher.setWatchDir(file);
		executorService.execute(fileWatcher);
	}

	private void registerAll(File file) throws IOException {
		Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				//ToDO: Propergate All Files 
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				//ToDO: Propergate All Files 
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	public void localFileChange(File file) {
		if (file.isDirectory()) {
			return;
		}

		PieFile pieFile = null;
		try {
			pieFile = this.fileUtilsService.getPieFile(file);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error Creating PieFile.", ex);
			return;
		}

		NewFileMessage msg = beanService.getBean(PieShareAppBeanNames.getNewFileMessageName());
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		msg.setPieFile(pieFile);
		try {
			clusterManagementService.sendMessage(msg, user.getCloudName());
			PieLogger.info(this.getClass(), "Send new file message. Filepath: {}", pieFile.getRelativeFilePath());
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "FileService error.", ex);
		}
		
		//Message New File
		//shareService.shareFile(file);
	}

	//todo: belongs into the fileRequestedTask not in here?
	@Override
	public void fileRequested(FileRequestMessage msg) {

		File file = new File(pieAppConfig.getWorkingDirectory(), msg.getPieFile().getRelativeFilePath());

		if (!file.exists()) {
			//if the file doesn't exist on this client it could be due the fact that itself
			//is requesting it right now
			requestService.checkForActiveFileHandle(msg.getPieFile());
			return;
		}
		
		//shareService.handleActiveShare(msg.getPieFile());

		PieFile pieFile = null;

		try {
			pieFile = this.fileUtilsService.getPieFile(file);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "File error.", ex);
			return;
		}

		if (hashService.isMD5Equal(msg.getPieFile().getMd5(), pieFile.getMd5())) {
			shareService.shareFile(file);
		}
		//todo: what happens when it is the "same file" with different MD5?!
	}

	@Override
	public boolean checkMergeFile(PieFile pieFile) {
		File file = new File(pieAppConfig.getWorkingDirectory(), pieFile.getRelativeFilePath());

		if (!file.exists()) {
			return true;
		}

		PieFile localPieFile = null;

		try {
			localPieFile = this.fileUtilsService.getPieFile(file);
		} catch (IOException ex) {
			//ToDo: DO conflict handling
			PieLogger.error(this.getClass(), "File error.", ex);
			return false;
		}

		if (!hashService.isMD5Equal(localPieFile.getMd5(), pieFile.getMd5())) {
			return true;
		}

		return false;
	}

	@Override
	public void handleObject(ClusterAddedEvent event) {
		try {
			//when a cluster is added this actually means that this client has entered a cloud
			//todo: request all files list!!!!
			//todo: fix hardcoded cluster name
			this.clusterManagementService.sendMessage(new FileListRequestMessage(),"sv");
		} catch (ClusterManagmentServiceException ex) {
			//todo: error handling
			PieLogger.error(this.getClass(), "File error.", ex);
		}
		//todo: unite FileService, CompareService and all other regarding FileHandling in one package
	}

	@Override
	public List<PieFile> getAllFilesList() throws IOException {
		List<PieFile> pieFiles = new ArrayList();
		
		//todo: maybe a own service or at least function?
		Files.walkFileTree(pieAppConfig.getWorkingDirectory().toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				File realFile = file.toFile();
				
				PieFile pieFile = fileUtilsService.getPieFile(realFile);
				pieFiles.add(pieFile);
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		return pieFiles;
	}

	@Override
	public void deleteRecursive(PieFile file) {
		File localFile = new File(this.pieAppConfig.getWorkingDirectory(), file.getRelativeFilePath());
		try {
			if(localFile.isDirectory()) {
				FileUtils.deleteDirectory(localFile);
			}
			else {
				localFile.delete();
			}
		}
		catch(IOException ex) {
			PieLogger.error(this.getClass(), "Deleting failed!", ex);
		}
	}
}
