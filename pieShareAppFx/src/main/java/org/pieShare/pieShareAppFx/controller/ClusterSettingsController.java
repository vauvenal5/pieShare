/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutFinished;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ClusterSettingsController implements Initializable {

	private IBeanService beanService;
	private PieExecutorService executorService;
	private ILogoutTask logoutTask;
	private MainSceneController mainSceneController;

	public void setMainSceneController(MainSceneController mainSceneController) {
		this.mainSceneController = mainSceneController;
	}

	public void setLogoutTask(ILogoutTask logoutTask) {
		this.logoutTask = logoutTask;
	}

	public void setExecuterService(PieExecutorService pieExecutorService) {
		this.executorService = pieExecutorService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@FXML
	private Label labelCloudName;

	@FXML
	private void handleLogoutAction(ActionEvent event) {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		LogoutCommand commnd = new LogoutCommand();
		commnd.setUserName(user.getCloudName());
		commnd.setCallback(new ILogoutFinished() {
			@Override
			public void finished() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							mainSceneController.setLoginControl();
						}
						catch (IOException ex) {
							PieLogger.error(this.getClass(), "Not able to set Login Control", ex);
						}
					}
				});
			}
		});
		logoutTask.setEvent(commnd);
		try {
			executorService.handlePieEvent(commnd);
		}
		catch (PieExecutorTaskFactoryException ex) {
			PieLogger.error(this.getClass(), "Error executing logout event", ex);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		labelCloudName.setText(user.getCloudName());
	}
}
