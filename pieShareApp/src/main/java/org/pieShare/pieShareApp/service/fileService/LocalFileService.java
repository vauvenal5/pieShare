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
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
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
public class LocalFileService extends FileServiceBase implements IClusterAddedListener{

	private IHashService hashService;
	private IClusterManagementService clusterManagementService;
	private IExecutorService executorService;
	private IFileWatcherService fileWatcher;

	public void setHashService(IHashService hashService) {
		this.hashService = hashService;
	}
	
	public void setFileWatcher(IFileWatcherService fileWatcher) {
		this.fileWatcher = fileWatcher;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}
	
	public void initFileService() {
		this.clusterManagementService.getClusterAddedEventBase().addEventListener(this);
	}
	
	private void addWatchDirectory(File file) {
		fileWatcher.setWatchDir(file);
		executorService.execute(fileWatcher);
	}
	
	@Override
	public void handleObject(ClusterAddedEvent event) {
		try {
			//when a cluster is added this actually means that this client has entered a cloud
			//todo: request all files list!!!!
			PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			configuration = user.getPieShareConfiguration();
			addWatchDirectory(configuration.getWorkingDir());
			this.clusterManagementService.sendMessage(new FileListRequestMessage(), user.getCloudName());
		}
		catch (ClusterManagmentServiceException ex) {
			//todo: error handling
			PieLogger.error(this.getClass(), "File error.", ex);
		}
		//todo: unite FileService, CompareService and all other regarding FileHandling in one package
	}

	@Override
	public List<PieFile> getAllFiles() throws IOException {
		//todo: try first to read DB
		List<PieFile> pieFiles = new ArrayList();

		//todo: maybe a own service or at least function?
		Files.walkFileTree(configuration.getWorkingDir().toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				File realFile = file.toFile();

				PieFile pieFile = getPieFile(realFile);
				pieFiles.add(pieFile);

				return FileVisitResult.CONTINUE;
			}
		});

		return pieFiles;
	}

	@Override
	public PieFile getPieFile(File file) throws FileNotFoundException, IOException {
		/*if (!file.exists()) {
		 throw new FileNotFoundException("File: " + file.getPath() + " does not exist");
		 }*/

		PieFile pieFile = beanService.getBean(PieShareAppBeanNames.getPieFileName());

		pieFile.setRelativeFilePath(relitivizeFilePath(file).toString());

		pieFile.setFileName(file.getName());
		pieFile.setLastModified(file.lastModified());

		if (file.exists()) {
			pieFile.setMd5(hashService.hashStream(file));
		}

		return pieFile;
	}

	@Override
	public PieFile getPieFile(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		return this.getPieFile(file);
	}
}
