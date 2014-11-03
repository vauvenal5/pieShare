/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.springConfiguration.PieShareApp;

import org.apache.commons.vfs2.FileListener;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.service.actionService.LoginActionService;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.service.comparerService.ComparerService;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.fileFilterService.FileFilterService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileListenerService.ApacheDefaultFileListener;
import org.pieShare.pieShareApp.service.fileListenerService.ApacheFileWatcher;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileService.FileService;
import org.pieShare.pieShareApp.service.fileService.FileUtilsService;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.loginService.LoginService;
import org.pieShare.pieShareApp.service.networkService.NetworkService;
import org.pieShare.pieShareApp.service.requestService.RequestService;
import org.pieShare.pieShareApp.service.shareService.BitTorrentService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.localTasks.LocalFileCreatedTask;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.ShutdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppService {

	@Autowired
	private PieUtilitiesConfiguration utilities;
	@Autowired
	private PiePlateConfiguration plate;

	@Bean
	@Lazy
	public LoginCommandService loginCommandService() {
		LoginCommandService service = new LoginCommandService();
		service.setBeanService(this.utilities.beanService());
		service.setLoginService(loginService());
		service.setExecuterService(utilities.pieExecutorService());
		return service;
	}

	@Bean
	@Lazy
	public LoginActionService loginActionService() {
		LoginActionService service = new LoginActionService();
		service.setBeanService(this.utilities.beanService());
		service.setCommandService(this.loginCommandService());
		return service;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public PieFile pieFile() {
		return new PieFile();
	}

	@Bean
	@Lazy
	public ShutdownService shutdownService() {
		ShutdownService service = new ShutdownService();
		service.setListener(this.utilities.pieExecutorService());
		return service;
	}

	@Bean
	public PieShareService pieShareService() {
		PieShareService service = new PieShareService();
		service.setBeanService(this.utilities.beanService());
		service.setClusterManagementService(this.plate.clusterManagementService());
		service.setExecutorService(this.utilities.pieExecutorService());
		service.setParserService(this.utilities.argparse4jService());
		service.setShutdownService(this.shutdownService());
		service.setDatabaseService(databaseService());
		service.start();
		return service;
	}

	@Bean
	@Lazy
	public NetworkService networkService() {
		return new NetworkService();
	}

	@Bean
	@Lazy
	public PieShareAppConfiguration pieShareAppConfiguration() {
		PieShareAppConfiguration service = new PieShareAppConfiguration();
		service.setConfigurationReader(this.utilities.configurationReader());
		service.init();
		return service;
	}

	@Bean
	@Lazy
	public RequestService requestService() {
		RequestService service = new RequestService();
		service.setBeanService(this.utilities.beanService());
		service.setClusterManagementService(this.plate.clusterManagementService());
		service.setShareService(this.shareService());
		return service;
	}

	@Bean
	@Lazy
	public ComparerService comparerService() {
		ComparerService service = new ComparerService();
		service.setFileUtilsService(this.fileUtilsService());
		service.setPieShareConfiguration(this.pieShareAppConfiguration());
		service.setRequestService(this.requestService());
		return service;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LocalFileCreatedTask fileCreatedTask() {
		LocalFileCreatedTask task = new LocalFileCreatedTask();
		task.setFileService(this.fileService());
		return task;
	}

	@Bean
	@Lazy
	public FileListener fileListener() {
		ApacheDefaultFileListener listener = new ApacheDefaultFileListener();
		listener.setBeanService(this.utilities.beanService());
		listener.setExecutorService(this.utilities.pieExecutorService());
		listener.init();
		return listener;
	}

	@Bean
	@Lazy
	public IFileListenerService fileListenerService() {
		return (ApacheDefaultFileListener) this.fileListener();
	}

	@Bean
	@Lazy
	public ApacheFileWatcher fileWatcher() {
		ApacheFileWatcher watcher = new ApacheFileWatcher();
		watcher.setFileListener(this.fileListener());
		return watcher;
	}

	@Bean
	public FileService fileService() {
		FileService service = new FileService();
		service.setBeanService(this.utilities.beanService());
		service.setClusterManagementService(this.plate.clusterManagementService());
		service.setExecutorService(this.utilities.pieExecutorService());
		service.setFileWatcher(this.fileWatcher());
		service.setMd5Service(this.utilities.md5Service());
		service.setPieShareAppConfiguration(this.pieShareAppConfiguration());
		service.setRequestService(this.requestService());
		service.setFileUtilsService(this.fileUtilsService());
		service.initFileService();
		return service;
	}

	@Bean
	@Lazy
	public BitTorrentService shareService() {
		BitTorrentService service = new BitTorrentService();
		service.setBase64Service(this.utilities.base64Service());
		service.setBeanService(this.utilities.beanService());
		service.setClusterManagementService(this.plate.clusterManagementService());
		service.setConfigurationService(this.pieShareAppConfiguration());
		service.setFileUtilsService(this.fileUtilsService());
		service.setNetworkService(this.networkService());
		service.setShutdownService(this.shutdownService());
		service.setTmpFolderService(this.utilities.tempFolderService());
		service.setFileListener(this.fileListenerService());
		service.bitTorrentServicePost();
		return service;
	}

	@Bean
	@Lazy
	public FileUtilsService fileUtilsService() {
		FileUtilsService service = new FileUtilsService();
		service.setBeanService(this.utilities.beanService());
		service.setHashService(this.utilities.md5Service());
		service.setPieAppConfig(this.pieShareAppConfiguration());
		service.setFileListener(this.fileListenerService());
		return service;
	}

	@Bean
	@Lazy
	public DatabaseService databaseService() {
		DatabaseService service = new DatabaseService();
		service.setPieShareAppConfiguration(pieShareAppConfiguration());
		service.setBase64Service(utilities.base64Service());
		service.setBeanService(utilities.beanService());
		return service;
	}

	@Bean
	@Lazy
	public FileFilterService fileFilterService() {
		FileFilterService filter = new FileFilterService();
		filter.setDatabaseService(databaseService());
		return filter;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public RegexFileFilter fileFilter() {
		RegexFileFilter filter = new RegexFileFilter();
		filter.setRegexService(utilities.regexService());
		return filter;
	}

	@Bean
	@Lazy
	public LoginService loginService() {
		LoginService service = new LoginService();
		service.setBeanService(utilities.beanService());
		service.setPasswordEncryptionService(utilities.passwordEncryptionService());
		service.setPieShareAppConfig(pieShareAppConfiguration());
		service.setEncodeService(utilities.encodeService());
		service.setDatabaseService(databaseService());
		service.setClusterManagementService(plate.clusterManagementService());
		service.setLoginFinishedEventBase(utilities.eventBase());
		return service;
	}

}
