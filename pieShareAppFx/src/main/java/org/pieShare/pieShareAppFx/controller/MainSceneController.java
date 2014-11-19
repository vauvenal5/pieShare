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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareAppFx.controller.api.IController;
import org.pieShare.pieShareAppFx.controller.api.ITwoColumnListViewItem;
import org.pieShare.pieShareAppFx.events.LoginStateChangedEvent;
import org.pieShare.pieShareAppFx.events.api.ILoginStateChangedListener;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
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
	private TwoColumnListViewController preferencesListViewController;
	private TwoColumnListViewController cloudsListViewController;
	private LoginController loginController;
	private ClusterSettingsController clusterSettingsController;

	@FXML
	private GridPane mainGridPane;

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	private BorderPane borderPaneClouds;

	@FXML
	private SplitPane mainSplitPane;

	@FXML
	private Accordion mainAccordion;

	@FXML
	private TitledPane titelPaneClouds;

	@FXML
	private Button addButton;

	@FXML
	private BorderPane preferencesBorderPane;

	public void setClusterSettingsController(ClusterSettingsController clusterSettingsController) {
		this.clusterSettingsController = clusterSettingsController;
	}

	public void setPreferencesListViewController(TwoColumnListViewController preferencesListViewController) {
		this.preferencesListViewController = preferencesListViewController;
	}

	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

	public void setCloudsListViewController(TwoColumnListViewController cloudsListViewController) {
		this.cloudsListViewController = cloudsListViewController;
	}

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

		mainGridPane.setGridLinesVisible(false);
		mainGridPane.getStyleClass().add("gridPane");
		mainBorderPane.setCenter(mainGridPane);

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());

		try {
			preferencesBorderPane.setCenter(preferencesListViewController.getControl());
			borderPaneClouds.setCenter(cloudsListViewController.getControl());
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error setting PreferencesListView to Main Scene", ex);
			return;
		}

		if (user.getCloudName() != null) {
			addButton.setDisable(true);
			cloudsListViewController.addItem(new ITwoColumnListViewItem() {

				@Override
				public Node getSecondColumn() {
					return new Label(user.getCloudName());
				}

				@Override
				public Node getFirstColumn() {
					return null;
				}

				@Override
				public IController getController() {
					return null;
				}
			});
		}

		mainAccordion.setExpandedPane(titelPaneClouds);

		for (int i = 1; i < mainAccordion.getPanes().size(); i++) {
			mainAccordion.getPanes().get(i).setDisable(!user.isIsLoggedIn());
		}

		mainSplitPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				mainSplitPane.setDividerPosition(0, 0.25f);
			}
		});

		clusterManagementService.getClusterAddedEventBase().addEventListener(new IClusterAddedListener() {
			@Override
			public void handleObject(ClusterAddedEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
						if (user.getCloudName() == null) {
							return;
						}
						cloudsListViewController.clearAll();
						cloudsListViewController.addItem(new ITwoColumnListViewItem() {

							@Override
							public Node getSecondColumn() {
								return new Label(user.getCloudName());
							}

							@Override
							public Node getFirstColumn() {
								return null;
							}

							@Override
							public IController getController() {
								return null;
							}
						});
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
						//refreshCloudList();
					}
				});
			}
		});

		preferencesListViewController.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (preferencesListViewController.getSelectedItem() != null) {
					try {
						setPreferencesControl(preferencesListViewController.getSelectedItem());
					}
					catch (IOException ex) {
						PieLogger.error(this.getClass(), "Error while setting controller", ex);
					}
				}
			}
		});

		cloudsListViewController.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (cloudsListViewController.getSelectedItem() != null) {
					try {
						setClusterSettingControl();
					}
					catch (IOException ex) {
						PieLogger.error(this.getClass(), "Not able to set ClusterSettings Control", ex);
					}
				}
			}
		});

		loginController.getLoginStateChangedEventBase().addEventListener(new ILoginStateChangedListener() {

			@Override
			public void handleObject(LoginStateChangedEvent event) {
				if (event.isIsLoggedIn()) {
					loginComplete();
				}
			}
		});

		clusterSettingsController.getLoginStateChangedEvent().addEventListener(new ILoginStateChangedListener() {

			@Override
			public void handleObject(LoginStateChangedEvent event) {
				if (!event.isIsLoggedIn()) {
					try {
						setLoginControl();
					}
					catch (IOException ex) {
						PieLogger.error(this.getClass(), "Error setting login control", ex);
					}
				}
			}
		});

		//Set entries for settings list view
		preferencesListViewController.addItem(beanService.getBean(FileFilterSettingsController.class));
		preferencesListViewController.addItem(beanService.getBean(BasePreferencesController.class));
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

	private void loginComplete() {
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

	public void setToMainCenter(Node node) {
		mainGridPane.getChildren().clear();
		mainGridPane.add(node, 0, 1);
	}

	public void setLoginControl() throws IOException {
		setToMainCenter(loginController.getControl());
	}

	public void setClusterSettingControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		if (user.isIsLoggedIn()) {
			setToMainCenter(clusterSettingsController.getControl());
		}
		else {
			setLoginControl();
		}
	}

	public void setPreferencesControl(ITwoColumnListViewItem entry) throws IOException {
		setToMainCenter(entry.getController().getControl());
	}
}
