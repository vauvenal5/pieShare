/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import org.pieShare.pieShareAppFx.controller.api.IController;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieshare.piespring.service.ApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieshare.piespring.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareAppFx.FXMLController;
import org.pieShare.pieShareAppFx.controller.api.ITwoColumnListViewItem;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class BasePreferencesController implements IController, ITwoColumnListViewItem {

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

	@Override
	public Node getControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		return loader.load(getClass().getResourceAsStream("/fxml/settingsPanels/BasePreferencesPanel.fxml"));
	}

	@Override
	public Label getSecondColumn() {
		Label label = new Label("Base Settings");
		return label;
	}

	@Override
	public Label getFirstColumn() {
		InputStream st = getClass().getResourceAsStream("/images/settings_16.png");
		Image image = new Image(st);
		Label label = new Label("", new ImageView(image));
		return label;
	}

	@Override
	public IController getController() {
		return this;
	}

}
