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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 * FXML Controller class
 *
 * @author Svetoslav
 */
public class MainSceneController implements Initializable {

	private ClusterSettingsController clusterSettingsController;
	private IBeanService beanService;

	@FXML
	private AnchorPane mainPane;

	@FXML
	private AnchorPane cloudsAnchorPane;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterSettingsController(ClusterSettingsController settingsController) {
		this.clusterSettingsController = settingsController;
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		InputStream cloudsListViewStream = getClass().getResourceAsStream("/fxml/CloudsListView.fxml");
		try {
			this.cloudsAnchorPane.getChildren().add(loader.load(cloudsListViewStream));
		} catch (IOException ex) {
			//ToDO: Handle
			Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	private void handleAddCloudAction(ActionEvent event) {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		this.mainPane.getChildren().clear();
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/Login.fxml");
			this.mainPane.getChildren().add(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}

	public void setClusterSettingControl(IClusterService cluster) {
		clusterSettingsController.setClusterFile(cluster);
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		this.mainPane.getChildren().clear();
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/settingsPanels/SettingsPanel.fxml");
			this.mainPane.getChildren().add(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}

}
