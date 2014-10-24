/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;

/**
 *
 * @author Richard
 */
public class ClusterSettingsController implements Initializable {

	@FXML
	private Label labelCloudName;
	public IClusterService clusterService;

	@FXML
	private void handleLogoutAction(ActionEvent event) {
		try {
			clusterService.disconnect();
		} catch (ClusterServiceException ex) {
			Logger.getLogger(ClusterSettingsController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setClusterFile(IClusterService clusterService) {
		this.clusterService = clusterService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelCloudName.setText(clusterService.getName());
	}

}
