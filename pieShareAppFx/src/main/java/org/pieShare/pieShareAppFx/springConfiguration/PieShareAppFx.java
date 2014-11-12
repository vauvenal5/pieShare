/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration;

import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import javafx.fxml.FXMLLoader;
import org.pieShare.pieShareAppFx.ControllerFactory;
import org.pieShare.pieShareAppFx.FXMLController;
import org.pieShare.pieShareAppFx.controller.BasePreferencesController;
import org.pieShare.pieShareAppFx.controller.CloudsListViewController;
import org.pieShare.pieShareAppFx.controller.ClusterSettingsController;
import org.pieShare.pieShareAppFx.controller.FileFilterSettingsController;
import org.pieShare.pieShareAppFx.controller.LoginController;
import org.pieShare.pieShareAppFx.controller.MainSceneController;
import org.pieShare.pieShareAppFx.entryModels.BasePreferencesEntry;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareAppFx.animations.SpinAnimation;
import org.pieShare.pieShareAppFx.controller.WorkingMessageController;
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
public class PieShareAppFx {

	@Autowired
	private PieUtilitiesConfiguration utilities;
	@Autowired
	private PieShareAppService services;
	@Autowired
	private PiePlateConfiguration plate;
	@Autowired
	private PieShareAppService appService;
	@Autowired
	private PieShareAppTasks tasks;

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public FXMLLoader fxmlLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(controllerFactory());
		return loader;
	}

	@Bean
	@Lazy
	public FXMLController mainController() {
		FXMLController controller = new FXMLController();
		controller.setBeanService(this.utilities.beanService());
		controller.setFXMLLoader(this.fxmlLoader());
		return controller;
	}

	@Bean
	@Lazy
	public MainSceneController mainSceneController() {
		MainSceneController controller = new MainSceneController();
		controller.setBeanService(this.utilities.beanService());
		controller.setClusterManagementService(plate.clusterManagementService());
		return controller;
	}

	@Bean
	@Lazy
	public ClusterSettingsController clusterSettingsController() {
		ClusterSettingsController controller = new ClusterSettingsController();
		controller.setBeanService(utilities.beanService());
		controller.setExecuterService(utilities.pieExecutorService());
		controller.setMainSceneController(mainSceneController());
		controller.setLogoutTask(tasks.logoutTask());
		return controller;
	}

	@Bean
	@Lazy
	public ControllerFactory controllerFactory() {
		ControllerFactory controller = new ControllerFactory();
		controller.setBeanService(utilities.beanService());
		return controller;
	}

	@Bean
	@Lazy
	public LoginController loginController() {
		LoginController controller = new LoginController();
		controller.setLoginTask(tasks.loginTask());
		controller.setPieExecutorService(utilities.pieExecutorService());
		controller.setBeanService(utilities.beanService());
		controller.setMainSceneController(mainSceneController());
		return controller;
	}

	@Bean
	@Lazy
	public CloudsListViewController cloudsListViewController() {
		CloudsListViewController controller = new CloudsListViewController();
		controller.setBeanService(utilities.beanService());
		controller.setClusterManagementService(plate.clusterManagementService());
		controller.setMainSceneController(mainSceneController());
		return controller;
	}

	@Bean
	@Lazy
	public BasePreferencesController basePreferencesController() {
		BasePreferencesController controller = new BasePreferencesController();
		controller.setFXMLController(this.mainController());
		controller.setBeanService(utilities.beanService());
		controller.setDatabaseService(services.databaseService());
		controller.setApplicationConfigurationService(services.applicationConfigurationService());
		return controller;
	}

	@Bean
	@Lazy
	public BasePreferencesEntry basePreferencesEntry() {
		BasePreferencesEntry controller = new BasePreferencesEntry();
		controller.setBasePreferencesController(basePreferencesController());
		return controller;
	}

	@Bean
	@Lazy
	public FileFilterSettingsController fileFilterSettingsController() {
		FileFilterSettingsController controller = new FileFilterSettingsController();
		controller.setRegexService(utilities.regexService());
		controller.setFileFilterService(services.fileFilterService());
		controller.setBeanService(utilities.beanService());
		controller.setFXMLController(mainController());
		controller.setFileService(services.localFileService());
		return controller;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public SpinAnimation spinAnimation() {
		SpinAnimation animation = new SpinAnimation();
		return animation;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public WorkingMessageController workingMessageController() {
		WorkingMessageController controller = new WorkingMessageController();
		controller.setBeanService(utilities.beanService());
		return controller;
	}

}
