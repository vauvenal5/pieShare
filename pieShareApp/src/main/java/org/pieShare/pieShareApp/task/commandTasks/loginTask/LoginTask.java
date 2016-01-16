/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.loginTask;

import java.io.File;
import java.util.Arrays;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieShareApp.service.loginService.UserTools;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Richard
 */
public class LoginTask implements ILoginTask {

    private final byte[] FILE_TEXT;
    private IPasswordEncryptionService passwordEncryptionService;
    private IEncodeService encodeService;
    private LoginCommand command;
    private IDatabaseService databaseService;
    private IClusterManagementService clusterManagementService;
    private IConfigurationFactory configurationFactory;
    private IHistoryService historyService;
    private IFileWatcherService fileWatcherService;
    private IMessageFactoryService messageFactoryService;
    private IUserService userService;
    private Provider<SymmetricEncryptedChannel> symmetricEncryptedChannelProvider;
    private UserTools userTools;
    private File pwdFile;

    public LoginTask() {
        this.FILE_TEXT = "FILE_TEXT".getBytes();
    }

    public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
        this.messageFactoryService = messageFactoryService;
    }

    public void setFileWatcherService(IFileWatcherService fileWatcherService) {
        this.fileWatcherService = fileWatcherService;
    }

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    public void setConfigurationFactory(IConfigurationFactory configurationFactory) {
        this.configurationFactory = configurationFactory;
    }

    public void setClusterManagementService(IClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    public void setDatabaseService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setSymmetricEncryptedChannelProvider(Provider<SymmetricEncryptedChannel> symmetricEncryptedChannelProvider) {
        this.symmetricEncryptedChannelProvider = symmetricEncryptedChannelProvider;
    }

    public void setPasswordEncryptionService(IPasswordEncryptionService service) {
        this.passwordEncryptionService = service;
    }

    public void setEncodeService(IEncodeService encodeService) {
        this.encodeService = encodeService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public void setUserTools(UserTools userTools) {
        this.userTools = userTools;
    }

    @Override
    public void setEvent(LoginCommand command) {
        this.command = command;
    }

    private void login() throws Exception {
        userService.getUser().setPieShareConfiguration(configurationFactory.checkAndCreateConfig(userService.getUser().getPieShareConfiguration(), true));
        pwdFile = userService.getUser().getPieShareConfiguration().getPwdFile();

        if (!pwdFile.exists()) {
            userTools.createUser(command.getPlainTextPassword());
        }

        if (!userTools.Login(command.getPlainTextPassword(), command.getUserName())) {
            throw new WrongPasswordException("Login Error");
        }

        this.historyService.syncLocalFilders();
		//todo-mr3: now we have to send a list with everything

        try {

            //listen to working dir
            this.fileWatcherService.watchDir(userService.getUser().getPieShareConfiguration().getWorkingDir());

            //todo: change this maybe in future to different aproach
				//should probably be coupled with network chang detection!
            //this is needed to recognize local changes on this node
            IFileListMessage fileList = this.messageFactoryService.getFileListMessage();
            fileList.getAddress().setClusterName(userService.getUser().getCloudName());
            fileList.getAddress().setChannelId(userService.getUser().getUserName());
            fileList.setFileList(this.historyService.getPieFiles());
			fileList.setFolderList(this.historyService.getPieFolders());
            this.clusterManagementService.sendMessage(fileList);

            //send file list request message to cluster
            FileListRequestMessage msg = this.messageFactoryService.getFileListRequestMessage();
            msg.getAddress().setClusterName(userService.getUser().getCloudName());
            msg.getAddress().setChannelId(userService.getUser().getUserName());
            this.clusterManagementService.sendMessage(msg);
        } catch (ClusterManagmentServiceException ex) {
            PieLogger.error(this.getClass(), "Connect failed!", ex);
        }
    }

    @Override
    public void run() {
        try {
            login();
            command.getCallback().OK();
        } catch (WrongPasswordException ex) {
            PieLogger.error(this.getClass(), "Error in login task!", ex);
            command.getCallback().wrongPassword(ex);
        } catch (Exception ex) {
            PieLogger.error(this.getClass(), "Error in login task!", ex);
            command.getCallback().error(ex);
        }
    }
}
