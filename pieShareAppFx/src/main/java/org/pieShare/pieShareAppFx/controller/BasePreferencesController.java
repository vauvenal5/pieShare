/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.configurationService.ApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareAppFx.FXMLController;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class BasePreferencesController implements Initializable {

	private IPieShareConfiguration configuration;
	private ApplicationConfigurationService applicationConfigurationService;
	private IBeanService beanService;
	private FXMLController fxmlController;
	private IDatabaseService databaseService;
	private PieUser user;

	@FXML
	private Button buttonBrowseWorking;

	@FXML
	private Button buttonBrowseTemp;

	@FXML
	private Button buttonBrowseDatabase;

	@FXML
	private TextField textFieldTempPath;

	@FXML
	private TextField textFieldWorkingPath;

	@FXML
	private TextField textFielddatabaseDir;

	@FXML
	private AnchorPane loginInnerContainer;

	@PostConstruct
	public void init() {

	}

	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFXMLController(FXMLController controller) {
		this.fxmlController = controller;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		configuration = user.getPieShareConfiguration();
		textFieldTempPath.setText(configuration.getTmpDir().getAbsolutePath());
		textFieldWorkingPath.setText(configuration.getWorkingDir().getAbsolutePath());
		textFielddatabaseDir.setText(applicationConfigurationService.getDatabaseFolder().toPath().toString());
	}

	@FXML
	private void handleButtonWorkingClick(ActionEvent event) {
		File choosenFile = showFolderChooser("Select Working Directory", configuration.getWorkingDir());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}
		configuration.setWorkingDir(choosenFile);
		textFieldWorkingPath.setText(configuration.getWorkingDir().getAbsolutePath());
		databaseService.mergePieUser(user);
	}

	@FXML
	private void handleButtonTempClick(ActionEvent event) {
		File choosenFile = showFolderChooser("Select Temp Directory", configuration.getTmpDir());
		if (choosenFile == null) {
			return;
		}
		configuration.setTmpDir(choosenFile);
		textFieldTempPath.setText(configuration.getTmpDir().getAbsolutePath());
		databaseService.mergePieUser(user);
	}

	@FXML
	private void handleButtonDatabaseClick(ActionEvent event) {
		File choosenFile = showFolderChooser("Select Database Directory", applicationConfigurationService.getDatabaseFolder());
		if (choosenFile == null) {
			return;
		}
		applicationConfigurationService.setDatabaseFolder(choosenFile);
	}

	private File showFolderChooser(String titel, File initial) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(titel);
		chooser.setInitialDirectory(initial);
		File choosenFile = chooser.showDialog(fxmlController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return null;
		}
		return choosenFile;
	}

}
