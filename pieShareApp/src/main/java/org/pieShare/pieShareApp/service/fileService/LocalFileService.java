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
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 * @author richy
 */
public class LocalFileService extends FileServiceBase {

	private IHashService hashService;

	public void setHashService(IHashService hashService) {
		this.hashService = hashService;
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
