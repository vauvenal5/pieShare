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
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareAppFx.conrolExtensions.TwoColumnListView;
import org.pieShare.pieShareAppFx.conrolExtensions.api.ITwoColumnListView;
import org.pieShare.pieShareAppFx.entryModels.BasePreferencesEntry;
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
	private Accordion mainAccordion;
	
	@FXML
	private TitledPane titelPaneClouds;
	
	@FXML
	private ListView<ITwoColumnListView> settingsListView;
	private ObservableList<ITwoColumnListView> settingsListViewItems;

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
		mainAccordion.setExpandedPane(titelPaneClouds);
		
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

		settingsListView.setCellFactory(new Callback<ListView<ITwoColumnListView>, ListCell<ITwoColumnListView>>() {
			@Override
			public ListCell<ITwoColumnListView> call(final ListView<ITwoColumnListView> param) {
				return new TwoColumnListView();
			}
		});

		//Set entries for settings list view
		settingsListViewItems.add(beanService.getBean("basePreferencesEntry"));
		settingsListViewItems.add(new ITwoColumnListView() {

			@Override
			public Node getSecondColumn() {
				return new Label("Filter Settings");
			}

			@Override
			public Node getFirstColumn() {
				InputStream st = getClass().getResourceAsStream("/images/filter_16.png");
				Image image = new Image(st);
				Label label = new Label("", new ImageView(image));
				return label;
			}

			@Override
			public String getPanelPath() {
				return "/fxml/settingsPanels/FileFilterSettingPanel.fxml";
			}
		});
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

	public void setPreferencesControl(ITwoColumnListView entry) {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		try {
			InputStream url = getClass().getResourceAsStream(entry.getPanelPath());
			mainBorderPane.setCenter(loader.load(url));
		} catch (IOException ex) {
			//ToDO: Handle
			ex.printStackTrace();
		}
	}
}
