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
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareAppFx.FXMLController;

/**
 *
 * @author Richard
 */
public class BasePreferencesController implements Initializable {

	private PieShareAppConfiguration pieShareAppConfig;
	private FXMLController fxmlController;

	@FXML
	private Button buttonBrowseWorking;

	@FXML
	private Button buttonBrowseTemp;
	
	@FXML
	private TextField textFieldTempPath;

	@FXML
	private TextField textFieldWorkingPath;

	public void setPieShareAppConfiguration(PieShareAppConfiguration pieShareAppConfig) {
		this.pieShareAppConfig = pieShareAppConfig;
	}

	public void setFXMLController(FXMLController controller) {
		this.fxmlController = controller;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		textFieldTempPath.setText(pieShareAppConfig.getTempCopyDirectory().getAbsolutePath());
		textFieldWorkingPath.setText(pieShareAppConfig.getWorkingDirectory().getAbsolutePath());
	}

	@FXML
	private void handleButtonWorkingClick(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Working Directory");
		chooser.setInitialDirectory(pieShareAppConfig.getWorkingDirectory());
		File choosenFile = chooser.showDialog(fxmlController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}
		pieShareAppConfig.setWorkingDir(choosenFile);
		textFieldWorkingPath.setText(pieShareAppConfig.getWorkingDirectory().getAbsolutePath());
	}

	@FXML
	private void handleButtonTempClick(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Temp Directory");
		chooser.setInitialDirectory(pieShareAppConfig.getTempCopyDirectory());
		File choosenFile = chooser.showDialog(fxmlController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}
		pieShareAppConfig.setTempCopyDir(choosenFile);
		textFieldTempPath.setText(pieShareAppConfig.getTempCopyDirectory().getAbsolutePath());
	}
}
