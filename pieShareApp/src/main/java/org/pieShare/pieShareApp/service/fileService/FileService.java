package org.pieShare.pieShareApp.service.fileService;

import org.pieShare.pieShareApp.model.pieFile.PieFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
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
	private IBeanService beanService;
	private IHashService hashService;
	private IRequestService requestService;
	private IClusterManagementService clusterManagementService;
	private IFileUtilsService fileUtilsService;
	private IPieShareConfiguration configuration;
	
	public FileService() {
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setMd5Service(IHashService hashService) {
		this.hashService = hashService;
	}

	@Override
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
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		configuration = user.getPieShareConfiguration();
		addWatchDirectory(configuration.getWorkingDir());

		this.clusterManagementService.getClusterAddedEventBase().addEventListener(this);
	}

	public void setFileWatcher(IFileWatcherService fileWatcher) {
		this.fileWatcher = fileWatcher;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
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
	public void waitUntilCopyFinished(String filePath) {
		File file = new File(filePath);
		FileInputStream st;
		boolean isCopying = true;

		while (isCopying) {

			try {
				Thread.sleep(2000);
				st = new FileInputStream(file);
				isCopying = false;
				st.close();
			}
			catch (FileNotFoundException ex) {
				//nothing needed to do here
			}
			catch (IOException ex) {
				//nothing needed to do here
			}
			catch (InterruptedException ex) {
				//nothing needed to do here
			}
		}
	}

	/*@Override
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
	 }*/
	@Override
	public void handleObject(ClusterAddedEvent event) {
		try {
			//when a cluster is added this actually means that this client has entered a cloud
			//todo: request all files list!!!!
			PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			this.clusterManagementService.sendMessage(new FileListRequestMessage(), user.getCloudName());
		}
		catch (ClusterManagmentServiceException ex) {
			//todo: error handling
			PieLogger.error(this.getClass(), "File error.", ex);
		}
		//todo: unite FileService, CompareService and all other regarding FileHandling in one package
	}

	@Override
	public List<PieFile> getAllFilesList() throws IOException {
		List<PieFile> pieFiles = new ArrayList();

		//todo: maybe a own service or at least function?
		Files.walkFileTree(configuration.getWorkingDir().toPath(), new SimpleFileVisitor<Path>() {
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
		File localFile = new File(this.configuration.getWorkingDir(), file.getRelativeFilePath());
		try {
			if (localFile.isDirectory()) {
				FileUtils.deleteDirectory(localFile);
			}
			else {
				localFile.delete();
			}
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Deleting failed!", ex);
		}
	}
}
