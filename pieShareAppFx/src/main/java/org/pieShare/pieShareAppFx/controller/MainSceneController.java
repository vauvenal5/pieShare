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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareAppFx.conrolExtensions.PreferencesListViewItems;
import org.pieShare.pieShareAppFx.preferences.api.IPreferencesEntry;
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
	private BorderPane mainBorderPane;

	@FXML
	private StackPane cloudsStackPane;

	@FXML
	private SplitPane mainSplitPane;

	@FXML
	private ListView<IPreferencesEntry> settingsListView;
	private ObservableList<IPreferencesEntry> settingsListViewItems;
	
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
		mainSplitPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				mainSplitPane.setDividerPosition(0, 0.25f);
			}
		});

		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		InputStream cloudsListViewStream = getClass().getResourceAsStream("/fxml/CloudsListView.fxml");
		try {
			this.cloudsStackPane.getChildren().add(loader.load(cloudsListViewStream));
		} catch (IOException ex) {
			//ToDO: Handle
			Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
		}

		settingsListViewItems = FXCollections.observableArrayList();

		settingsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (settingsListView.getSelectionModel().getSelectedItems() != null) {
					setPreferencesControl(settingsListView.getSelectionModel().getSelectedItem());
				}
			}
		});

		settingsListView.setCellFactory(new Callback<ListView<IPreferencesEntry>, ListCell<IPreferencesEntry>>() {
			@Override
			public ListCell<IPreferencesEntry> call(final ListView<IPreferencesEntry> param) {
				return new PreferencesListViewItems();
			}
		});

		//Set entries for settings list view
		settingsListViewItems.add(beanService.getBean("basePreferencesEntry"));
		settingsListView.setItems(settingsListViewItems);
	}

	@FXML
	private void handleAddCloudAction(ActionEvent event) {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/Login.fxml");
			mainBorderPane.setCenter(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}

	public void setClusterSettingControl(IClusterService cluster) {
		clusterSettingsController.setClusterFile(cluster);
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/settingsPanels/CloudsSettingsPanel.fxml");
			mainBorderPane.setCenter(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}

	public void setPreferencesControl(IPreferencesEntry cluster) {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/settingsPanels/BasePreferencesPanel.fxml");
			mainBorderPane.setCenter(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}
}
