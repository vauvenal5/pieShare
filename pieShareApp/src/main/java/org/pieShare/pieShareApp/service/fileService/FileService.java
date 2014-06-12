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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.task.FileMetaTask;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 * @author richy
 */
public class FileService implements IFileService
{

	private final PieLogger logger = new PieLogger(FileService.class);
	private IExecutorService executorService = null;
	private IFileWatcherService fileWatcher;
	private IPieShareAppConfiguration pieAppConfig;
	private IBeanService beanService;
	private IShareService shareService;
	private IHashService hashService;

	public FileService()
	{

	}

	public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
	{
		this.pieAppConfig = pieShareAppConfiguration;
	}

	public void setBeanService(IBeanService beanService)
	{
		this.beanService = beanService;
	}

	public void setShareService(IShareService shareService)
	{
		this.shareService = shareService;
	}

	public void setMd5Service(IHashService hashService)
	{
		this.hashService = hashService;
	}

	@PostConstruct
	public void initFileService()
	{
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

	public void setFileWatcher(IFileWatcherService fileWatcher)
	{
		this.fileWatcher = fileWatcher;
	}

	public void setExecutorService(IExecutorService executorService)
	{
		this.executorService = executorService;
		this.executorService.registerTask(FileTransferMetaMessage.class, FileMetaTask.class);
	}

	private void addWatchDirectory(File file)
	{
		fileWatcher.setWatchDir(file);
		executorService.execute(fileWatcher);
	}

	private void registerAll(File file) throws IOException
	{
		Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				//ToDO: Propergate All Files 
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				//ToDO: Propergate All Files 
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	public void localFileChange(File file)
	{
		shareService.shareFile(file);

	}

	@Override
	public boolean checkMergeFile(PieFile pieFile)
	{
		File file = new File(pieAppConfig.getWorkingDirectory(), pieFile.getRelativeFilePath());

		if (!file.exists())
		{
			return true;
		}

		
		
		//TODO: Warnung es kopiert die files immer.
		return true;
	}

	@Override
	public PieFile genPieFile(File file) throws FileNotFoundException, IOException
	{

		if (!file.exists())
		{
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
