/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.historyService;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.DAOs.PieFileDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieFolderDAO;
import org.pieShare.pieShareApp.service.database.DatabaseCreator;
import org.pieShare.pieShareApp.service.database.DatabaseFactory;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.database.ModelEntityConverterService;
import org.pieShare.pieShareApp.service.database.PieFilderDBService;
import org.pieShare.pieShareApp.service.fileService.FileUtilitiesService;
import org.pieShare.pieShareApp.service.fileService.LocalFileService;
import org.pieShare.pieShareApp.service.folderService.FolderService;
import org.pieShare.pieShareApp.service.userService.UserService;
import org.pieShare.pieTools.pieUtilities.service.security.BouncyCastleProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.MD5Service;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class HistoryServiceNGTest {

	HistoryService historyService;
	DatabaseService databaseService;
        PieFilderDBService pieFilderDBService;
	UserService userService;
	PieUser user;
	File testRoot;
	File testWorkingDir;
	LocalFileService fileService;
	FolderService folderService;
	DatabaseFactory fac;

	public HistoryServiceNGTest() {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		testRoot = new File("test");
		
		if(testRoot.exists()) {
			FileUtils.deleteDirectory(testRoot);
		}
		
		testRoot.mkdirs();
		testWorkingDir = new File(testRoot, "workingDir");
		testWorkingDir.mkdirs();

		IApplicationConfigurationService applicationConfigurationService = Mockito.mock(IApplicationConfigurationService.class);
		Mockito.when(applicationConfigurationService.getDatabaseFolder()).thenReturn(testRoot);

		//todo: would be cool if we can set the type from outside so that we can have a pure in memory DB for the tests
		fac = new DatabaseFactory();
		fac.setApplicationConfigurationService(applicationConfigurationService);
		fac.setCreator(new DatabaseCreator());
		fac.init();

		PieShareConfiguration config = new PieShareConfiguration();
		config.setPwdFile(null);
		config.setWorkingDir(testWorkingDir);

		user = new PieUser();
		user.setHasPasswordFile(false);
		user.setIsLoggedIn(true);
		user.setUserName("testUser");
		user.setPieShareConfiguration(config);

		this.userService = Mockito.mock(UserService.class);
		Mockito.when(this.userService.getUser()).thenReturn(user);

		ModelEntityConverterService converterService = new ModelEntityConverterService();
		converterService.setUserService(userService);

		PieFileDAO fileDao = new PieFileDAO();
		fileDao.setDatabaseFactory(fac);

		PieFolderDAO folderDao = new PieFolderDAO();
		folderDao.setDatabaseFactory(fac);

                MD5Service hashService = new MD5Service();
		hashService.setProviderService(new BouncyCastleProviderService());

                
                fileService = new LocalFileService();
		fileService.setUserService(userService);
		fileService.setHashService(hashService);
		fileService.setPieFileProvider(new Provider<PieFile>() {
			@Override
			public PieFile get() {
				return new PieFile();
			}
		});

		folderService = new FolderService();
		folderService.setUserService(userService);
                
                this.pieFilderDBService = new PieFilderDBService();
                this.pieFilderDBService.setConverterService(converterService);
                this.pieFilderDBService.setFileService(fileService);
                this.pieFilderDBService.setFolderService(folderService);
                this.pieFilderDBService.setPieFileDAO(fileDao);
                this.pieFilderDBService.setPieFolderDAO(folderDao);
                this.pieFilderDBService.setUserService(userService);
                
		this.databaseService = new DatabaseService();
		this.databaseService.setConverterService(converterService);
		this.databaseService.setPieFilderDBService(pieFilderDBService);
		
		FileUtilitiesService fileUtilitiesService = new FileUtilitiesService();
		fileUtilitiesService.setFileService(fileService);
		fileUtilitiesService.setFolderService(folderService);

		this.historyService = new HistoryService();
		this.historyService.setDatabaseService(databaseService);
		this.historyService.setFileService(fileService);
		this.historyService.setFileUtilitiesService(fileUtilitiesService);
		this.historyService.setUserService(userService);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		Statement statement = fac.getDatabaseConnection().createStatement();
		statement.executeQuery("DROP SCHEMA PUBLIC CASCADE");
		fac.closeDB();
                fac = null; 
                databaseService = null; 
                pieFilderDBService = null;
		//FileUtils.deleteDirectory(testRoot);
	}

	@Test
	public void shouldSyncUpdateAndRetrievePieFiles() {
		//add a new file to the history
		PieFile file = new PieFile();
		file.setDeleted(false);
		file.setLastModified(new Date().getTime());
		file.setMd5("test".getBytes());
		file.setName("myTestFile");
		file.setRelativePath("myTestFile");

		this.historyService.syncPieFile(file);

		PieFile actual = this.historyService.getPieFile(file.getRelativePath());
		Assert.assertEquals(actual, file);

		//update the history of the file
		file.setLastModified(new Date().getTime());
		file.setDeleted(true);
		this.historyService.syncPieFile(file);

		actual = this.historyService.getPieFile(file.getRelativePath());
		Assert.assertEquals(actual, file);
	}

	@Test
	public void shouldSyncUpdateAndRetrievePieFolder() {
		//add a new folder to the history	
		PieFolder folder = new PieFolder();
		folder.setDeleted(false);
		folder.setName("folder1");
		folder.setRelativePath("folder1");

		this.historyService.syncPieFolder(folder);

		PieFolder actual = this.historyService.getPieFolder(folder.getRelativePath());
		Assert.assertEquals(actual, folder);

		//update the history of the folder
		folder.setDeleted(true);
		this.historyService.syncPieFolder(folder);

		actual = this.historyService.getPieFolder(folder.getRelativePath());
		Assert.assertEquals(actual, folder);
	}

	@Test
	public void shouldSyncLocalDirWithHistory() throws Exception {
		
		File dirA = new File(testWorkingDir, "A");
		dirA.mkdirs();
		File dirB = new File(testWorkingDir, "B");
		dirB.mkdirs();

		RandomAccessFile fileA = new RandomAccessFile(new File(dirA, "file"), "rw");
		fileA.writeBytes("This is my test file A!!!");
		fileA.close();

		RandomAccessFile fileB = new RandomAccessFile(new File(dirB, "file"), "rw");
		fileB.writeBytes("This is my test file B!!!");
		fileB.close();
		
		PieFile pieFileA = this.fileService.getPieFile(new File(dirA, "file"));
		PieFile pieFileB = this.fileService.getPieFile(new File(dirB, "file"));
		PieFolder pieFolderA = this.folderService.getPieFolder(dirA);
		PieFolder pieFolderB = this.folderService.getPieFolder(dirB);
		
		this.historyService.setDateProvider(new Provider<Date>() {
			@Override
			public Date get() {
				return new Date();
			}
		});

		this.historyService.syncLocalFilders();
		
		//todo: for some reason the deleted flag is true after the persist
		List<PieFile> pieFiles = this.historyService.getPieFiles(pieFileA.getMd5());
		Assert.assertEquals(pieFiles.size(), 1);
		Assert.assertTrue(pieFiles.contains(pieFileA));
		
		pieFiles = this.historyService.getPieFiles();
		Assert.assertEquals(pieFiles.size(), 2);
		Assert.assertTrue(pieFiles.contains(pieFileA));
		Assert.assertTrue(pieFiles.contains(pieFileB));
		
		List<PieFolder> pieFolders = this.historyService.getPieFolders();
		Assert.assertEquals(pieFolders.size(), 2);
		Assert.assertTrue(pieFolders.contains(pieFolderA));
		Assert.assertTrue(pieFolders.contains(pieFolderB));
		
		FileUtils.deleteDirectory(new File(testWorkingDir, "A"));
		
		final Date dateDelete = new Date();
		this.historyService.setDateProvider(new Provider<Date>() {
			@Override
			public Date get() {
				return dateDelete;
			}
		});
		
		pieFileA.setDeleted(true);
		pieFileA.setLastModified(dateDelete.getTime());
		pieFolderA.setDeleted(true);
		pieFolderA.setLastModified(dateDelete.getTime());
		
		this.historyService.syncLocalFilders();
		
		pieFiles = this.historyService.getPieFiles(pieFileA.getMd5());
		Assert.assertEquals(pieFiles.size(), 1);
		Assert.assertTrue(pieFiles.contains(pieFileA));
		
		pieFiles = this.historyService.getPieFiles();
		Assert.assertEquals(pieFiles.size(), 2);
		Assert.assertTrue(pieFiles.contains(pieFileA));
		Assert.assertTrue(pieFiles.contains(pieFileB));
		
		pieFolders = this.historyService.getPieFolders();
		Assert.assertEquals(pieFolders.size(), 2);
		Assert.assertTrue(pieFolders.contains(pieFolderA));
		Assert.assertTrue(pieFolders.contains(pieFolderB));
	}
}
