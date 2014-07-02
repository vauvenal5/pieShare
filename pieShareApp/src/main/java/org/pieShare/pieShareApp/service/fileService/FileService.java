package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.model.task.FileMetaTask;
import org.pieShare.pieShareApp.model.task.FileRequestTask;
import org.pieShare.pieShareApp.model.task.NewFileTask;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 * @author richy
 */
public class FileService implements IFileService {

	private final PieLogger logger = new PieLogger(FileService.class);
	private IExecutorService executorService = null;
	private IFileWatcherService fileWatcher;
	private IPieShareAppConfiguration pieAppConfig;
	private IBeanService beanService;
	private IShareService shareService;
	private IHashService hashService;
	private IComparerService comparerService;

	public FileService() {

	}

	public void setComparerService(IComparerService comparerService) {
		this.comparerService = comparerService;
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

	@PostConstruct
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

	}

	public void setFileWatcher(IFileWatcherService fileWatcher) {
		this.fileWatcher = fileWatcher;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
		this.executorService.registerTask(FileTransferMetaMessage.class, FileMetaTask.class);
		this.executorService.registerExtendedTask(FileRequestMessage.class, FileRequestTask.class);
		this.executorService.registerExtendedTask(NewFileMessage.class, NewFileTask.class);
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
			pieFile = genPieFile(file);
		} catch (IOException ex) {
			logger.error("Error Creating PieFile. Message: " + ex.getMessage());
			return;
		}

		NewFileMessage msg = beanService.getBean(PieShareAppBeanNames.getNewFileMessageName());
		msg.setPieFile(pieFile);
		logger.info("Send new file message. Filepath:" + pieFile.getRelativeFilePath());
		//Message New File
		//shareService.shareFile(file);
	}

	@Override
	public void remoteFileChanged(NewFileMessage msg) {
		try {
			comparerService.comparePieFile(msg.getPieFile());
		} catch (IOException ex) {
			//TODO: Handle
		} catch (FileConflictException ex) {
			//TODO: Handle
		}
	}

	@Override
	public void fileRequested(FileRequestMessage msg) {

		File file = new File(pieAppConfig.getWorkingDirectory(), msg.getPieFile().getRelativeFilePath());

		if (!file.exists()) {
			return;
		}

		PieFile pieFile = null;

		try {
			pieFile = genPieFile(file);
		} catch (IOException ex) {
			return;
		}

		if (hashService.isMD5Equal(msg.getPieFile().getMd5(), pieFile.getMd5())) {
			shareService.shareFile(file);
		}
	}

	@Override
	public boolean checkMergeFile(PieFile pieFile
	) {
		File file = new File(pieAppConfig.getWorkingDirectory(), pieFile.getRelativeFilePath());

		if (!file.exists()) {
			return true;
		}

		PieFile localPieFile = null;

		try {
			localPieFile = genPieFile(file);
		} catch (IOException ex) {
			//ToDo: DO conflict hadling
			return false;
		}

		if (!hashService.isMD5Equal(localPieFile.getMd5(), pieFile.getMd5())) {
			return true;
		}

		return false;
	}

	@Override
	public PieFile genPieFile(File file) throws FileNotFoundException, IOException {

		if (!file.exists()) {
			throw new FileNotFoundException("File: " + file.getPath() + " does not exist");
		}

		PieFile pieFile = beanService.getBean(PieShareAppBeanNames.getPieFileName());

		Path pathBase = pieAppConfig.getWorkingDirectory().toPath();//new File(pieAppConfig.getWorkingDirectory().getAbsolutePath()).toPath();
		Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
		Path pathRelative = pathBase.relativize(pathAbsolute);
		pieFile.setRelativeFilePath(pathRelative.toString());

		pieFile.setLastModified(file.lastModified());
		pieFile.setFileName(file.getName());

		pieFile.setMd5(hashService.hashStream(new FileInputStream(file)));

		return pieFile;

	}

}
