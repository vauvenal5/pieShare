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
import javafx.stage.DirectoryChooser;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
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
	private IBeanService beanService;
	private FXMLController fxmlController;
	private IDatabaseService databaseService;
	private PieUser user;

	@FXML
	private Button buttonBrowseWorking;

	@FXML
	private Button buttonBrowseTemp;

	@FXML
	private TextField textFieldTempPath;

	@FXML
	private TextField textFieldWorkingPath;

	@PostConstruct
	public void init() {
		
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
	}

	@FXML
	private void handleButtonWorkingClick(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Working Directory");
		chooser.setInitialDirectory(configuration.getWorkingDir());
		File choosenFile = chooser.showDialog(fxmlController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}
		configuration.setWorkingDir(choosenFile);
		textFieldWorkingPath.setText(configuration.getWorkingDir().getAbsolutePath());
		databaseService.mergePieUser(user);
	}

	@FXML
	private void handleButtonTempClick(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Temp Directory");
		chooser.setInitialDirectory(configuration.getTmpDir());
		File choosenFile = chooser.showDialog(fxmlController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}
		configuration.setTmpDir(choosenFile);
		textFieldTempPath.setText(configuration.getTmpDir().getAbsolutePath());
		databaseService.mergePieUser(user);
	}
}
