/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
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
	private ListView<IClusterService> cloudsListView;
	private ObservableList<IClusterService> listItems;

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listItems = FXCollections.observableArrayList();
		cloudsListView.setItems(listItems);

		cloudsListView.setCellFactory(new Callback<ListView<IClusterService>, ListCell<IClusterService>>() {
			@Override
			public ListCell<IClusterService> call(ListView<IClusterService> p) {
				ListCell<IClusterService> cell = new ListCell<IClusterService>() {
					@Override
					protected void updateItem(IClusterService t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getName());
						}
					}
				};
				return cell;
			}
		});

		cloudsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<IClusterService>() {
			@Override
			public void changed(ObservableValue<? extends IClusterService> observable, IClusterService oldValue, IClusterService newValue) {
				if (newValue != null) {
					mainSceneController.setClusterSettingControl(newValue);
				}
			}
		});

		clusterManagementService.getClusterAddedEventBase().addEventListener(new IClusterAddedListener() {

			@Override
			public void handleObject(ClusterAddedEvent event) {
				refreshCloudList();
			}
		});

		clusterManagementService.getClusterRemovedEventBase().addEventListener(new IClusterRemovedListener() {

			@Override
			public void handleObject(ClusterRemovedEvent ClusterRemovedEvent) {
				refreshCloudList();
			}
		});

	}

	private void refreshCloudList() {
		listItems.clear();
		clusterManagementService.getClusters().entrySet().stream().forEach((cluster) -> {
			listItems.add(cluster.getValue());
		});
	}
}
