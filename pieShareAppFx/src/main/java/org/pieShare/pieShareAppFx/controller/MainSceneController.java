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
import javafx.scene.control.Button;
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
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareAppFx.conrolExtensions.TwoColumnListView;
import org.pieShare.pieShareAppFx.conrolExtensions.api.ITwoColumnListView;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * FXML Controller class
 *
 * @author Svetoslav
 */
public class MainSceneController implements Initializable {

	private IClusterManagementService clusterManagementService;
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
	private Button addButton;

	@FXML
	private ListView<ITwoColumnListView> settingsListView;
	private ObservableList<ITwoColumnListView> settingsListViewItems;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mainAccordion.setExpandedPane(titelPaneClouds);

		PieUser user = beanService.getBean(PieUser.class);

		for (int i = 1; i < mainAccordion.getPanes().size(); i++) {
			mainAccordion.getPanes().get(i).setDisable(!user.isIsLoggedIn());
		}

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
		}
		catch (IOException ex) {
			//ToDO: Handle
			Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
		}

		settingsListViewItems = FXCollections.observableArrayList();

		settingsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (settingsListView.getSelectionModel().getSelectedItems() != null) {
					try {
						setPreferencesControl(settingsListView.getSelectionModel().getSelectedItem());
					}
					catch (IOException ex) {
						PieLogger.error(this.getClass(), "Error while setting controller", ex);
					}
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

	public void cloudAvailable(boolean isAvailabe) {
		addButton.setDisable(isAvailabe);
	}

	@FXML
	private void handleAddCloudAction(ActionEvent event) {
		try {
			setLoginControl();
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Not able to set login control", ex);
		}
	}

	public void loginComplete()
	{
		try {
			setClusterSettingControl();
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Not able to set login cluster settings control", ex);
		}
		PieUser user = beanService.getBean(PieUser.class);

		for (int i = 1; i < mainAccordion.getPanes().size(); i++) {
			mainAccordion.getPanes().get(i).setDisable(!user.isIsLoggedIn());
		}
	}
	
	public InputStream getLoginControl() {
		InputStream st = getClass().getResourceAsStream("/fxml/Login.fxml");
		return st;
	}

	public InputStream getClusterSettingControl() {
		return getClass().getResourceAsStream("/fxml/settingsPanels/CloudsSettingsPanel.fxml");
	}

	public void setToMainCenter(Node node) {
		mainBorderPane.setCenter(node);
	}

	public void setLoginControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		setToMainCenter(loader.load(getLoginControl()));
	}

	public void setClusterSettingControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		if (user.isIsLoggedIn()) {
			setToMainCenter(loader.load(getClusterSettingControl()));
		}
		else {
			setToMainCenter(loader.load(getLoginControl()));
		}
	}

	public void setPreferencesControl(ITwoColumnListView entry) throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		InputStream url = getClass().getResourceAsStream(entry.getPanelPath());
		setToMainCenter((loader.load(url)));
	}
}
