/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutFinished;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutTask;
import org.pieShare.pieShareAppFx.controller.api.IController;
import org.pieShare.pieShareAppFx.events.LoginStateChangedEvent;
import org.pieShare.pieShareAppFx.events.api.ILoginStateChangedListener;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ClusterSettingsController implements IController {

	private IBeanService beanService;
	private PieExecutorService executorService;
	private ILogoutTask logoutTask;
	private IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> loginStateChanged;

	public IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> getLoginStateChangedEvent() {
		return loginStateChanged;
	}

	public void setLoginStateChangedEvent(IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> loginStateChanged) {
		this.loginStateChanged = loginStateChanged;
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
						loginStateChanged.fireEvent(new LoginStateChangedEvent(this, false));
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

	@Override
	public Node getControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		InputStream st = getClass().getResourceAsStream("/fxml/settingsPanels/CloudsSettingsPanel.fxml");
		return loader.load(st);
	}
}
