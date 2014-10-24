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
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class ClusterSettingsController implements Initializable {

	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	@FXML
	private Label labelCloudName;

	@FXML
	private void handleLogoutAction(ActionEvent event) {
		try {
			clusterManagementService.diconnectAll();
		} catch (ClusterManagmentServiceException ex) {
			Logger.getLogger(ClusterSettingsController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		labelCloudName.setText(user.getCloudName());
	}

}
