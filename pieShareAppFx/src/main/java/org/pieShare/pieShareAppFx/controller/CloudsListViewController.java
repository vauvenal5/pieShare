/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class CloudsListViewController implements Initializable {

	private IBeanService beanService;
	private MainSceneController mainSceneController;

	public void setMainSceneController(MainSceneController mainSceneController) {
		this.mainSceneController = mainSceneController;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@FXML
	private AnchorPane mainPane;

	private IClusterManagementService clusterManagementService;

	@FXML
	private ListView<PieUser> cloudsListView;
	private ObservableList<PieUser> listItems;

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listItems = FXCollections.observableArrayList();
		cloudsListView.setItems(listItems);

		cloudsListView.setCellFactory(new Callback<ListView<PieUser>, ListCell<PieUser>>() {
			@Override
			public ListCell<PieUser> call(ListView<PieUser> p) {
				ListCell<PieUser> cell = new ListCell<PieUser>() {
					@Override
					protected void updateItem(PieUser t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getCloudName());
						}
					}
				};
				return cell;
			}
		});

		cloudsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (cloudsListView.getSelectionModel().getSelectedItems() != null) {
					mainSceneController.setClusterSettingControl();
				}
			}
		});

		clusterManagementService.getClusterAddedEventBase().addEventListener(new IClusterAddedListener() {
			@Override
			public void handleObject(ClusterAddedEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						refreshCloudList();
					}
				});
			}
		});

		clusterManagementService.getClusterRemovedEventBase().addEventListener(new IClusterRemovedListener() {
			@Override
			public void handleObject(ClusterRemovedEvent ClusterRemovedEvent) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						refreshCloudList();
					}
				});
			}
		});
		refreshCloudList();
	}

	private void refreshCloudList() {
		listItems.clear();
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		if (user.getCloudName() != null) {
			listItems.add(user);
		}
	}
}
