/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareAppFx.FXMLController;
import org.pieShare.pieShareAppFx.conrolExtensions.TwoColumnListViewEntry;
import org.pieShare.pieShareAppFx.controller.api.IController;
import org.pieShare.pieShareAppFx.controller.api.ITwoColumnListViewItem;
import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.regexService.IRegexService;

/**
 *
 * @author Richard
 */
public class FileFilterSettingsController implements IController, ITwoColumnListViewItem {

	private FXMLController fXMLController;
	private boolean isCorrectRegex = false;
	private IRegexService regexService;
	private IFileFilterService fileFilterService;
	private IBeanService beanService;
	private IFileService fileService;
	private IPieShareConfiguration configuration;
	private IUserService userService;
	
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@FXML
	private ListView<ITwoColumnListViewItem> listViewFilters;

	@FXML
	private TextField patternTextField;

	@FXML
	private Button buttonSelectDir;

	@FXML
	private Button buttonSelectFile;

	@FXML
	private Button buttonAdd;

	@FXML
	private Button buttonDelete;

	private ObservableList<ITwoColumnListViewItem> listItems;

	@PostConstruct
	public void init() {
		PieUser user = userService.getUser();
		configuration = user.getPieShareConfiguration();
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFXMLController(FXMLController fXMLController) {
		this.fXMLController = fXMLController;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setRegexService(IRegexService regexService) {
		this.regexService = regexService;
	}

	public void setFileFilterService(IFileFilterService fileFilterService) {
		this.fileFilterService = fileFilterService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonAdd.disableProperty().set(true);

		buttonSelectFile.setText("");
		InputStream stFile = getClass().getResourceAsStream("/images/fileIcon_24.png");
		Image imageFile = new Image(stFile);
		buttonSelectFile.setGraphic(new ImageView(imageFile));

		buttonSelectDir.setText("");
		InputStream stDir = getClass().getResourceAsStream("/images/dirIcon_24.png");
		Image imageDir = new Image(stDir);
		buttonSelectDir.setGraphic(new ImageView(imageDir));

		buttonAdd.setText("");
		InputStream stAdd = getClass().getResourceAsStream("/images/add_24.png");
		Image imageAdd = new Image(stAdd);
		buttonAdd.setGraphic(new ImageView(imageAdd));

		buttonDelete.setText("");
		InputStream stDelete = getClass().getResourceAsStream("/images/remove_24.png");
		Image imageDelete = new Image(stDelete);
		buttonDelete.setGraphic(new ImageView(imageDelete));

		listItems = FXCollections.observableArrayList();
		listViewFilters.setItems(listItems);

		listViewFilters.setCellFactory(new Callback<ListView<ITwoColumnListViewItem>, ListCell<ITwoColumnListViewItem>>() {
			@Override
			public ListCell<ITwoColumnListViewItem> call(final ListView<ITwoColumnListViewItem> param) {
				return new TwoColumnListViewEntry();
			}
		});

		listViewFilters.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (listViewFilters.getSelectionModel().getSelectedItems() != null) {
					//TODO FILTER
					//patternTextField.setText(((IFilter) listViewFilters.getSelectionModel().getSelectedItem().getObject()).getPattern());
				}
			}
		});

		refreshList();

		patternTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				//Clear all Styles. 
				patternTextField.getStyleClass().clear();

				if (patternTextField.textProperty().isEmpty().get()) {
					buttonAdd.disableProperty().set(true);
					patternTextField.getStyleClass().add("textfieldWrong");
					return;
				}

				try {
					regexService.setPattern(s2);
				}
				catch (Exception ex) {
					buttonAdd.disableProperty().set(true);
					patternTextField.getStyleClass().add("textfieldWrong");
					return;
				}

				patternTextField.getStyleClass().add("textfieldOK");
				buttonAdd.disableProperty().set(false);
			}
		});
	}

	@FXML
	private void handleAddAction(ActionEvent event) {
		String text = patternTextField.getText();
		IFilter fil = beanService.getBean(RegexFileFilter.class);
		//TODO FILTER
		//fil.setPattern(text);
		fileFilterService.addFilter(fil);
		refreshList();
	}

	@FXML
	private void handleSelectDirAction(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select path to ignore");
		chooser.setInitialDirectory(configuration.getWorkingDir());
		File choosenFile = chooser.showDialog(fXMLController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}

		String relative = fileService.relativizeFilePath(choosenFile);

		patternTextField.setText(String.format("%s/%s", relative, ".*"));
	}

	@FXML
	private void handleSelectFileAction(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select path to ignore");
		chooser.setInitialDirectory(configuration.getWorkingDir());

		File choosenFile = chooser.showOpenDialog(fXMLController.getMainStage());
		if (choosenFile == null || !choosenFile.exists()) {
			return;
		}

		String relative = fileService.relativizeFilePath(choosenFile);

		patternTextField.setText(relative);
	}

	@FXML
	private void handleClearTextAction(ActionEvent event) {
		patternTextField.clear();
	}

	@FXML
	private void handleDeleteAction(ActionEvent event) {

		if (listViewFilters.getSelectionModel().getSelectedItem() != null) {
			fileFilterService.removeFilter((IFilter) listViewFilters.getSelectionModel().getSelectedItem().getObject());
		}
		refreshList();
	}

	public void refreshList() {
		listItems.clear();
		for (IFilter filter : fileFilterService.getAllFilters()) {
			listItems.add(new ITwoColumnListViewItem() {

				@Override
				public Object getObject() {
					return filter; //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public Label getSecondColumn() {
					//TODO FILTER
					Label label = new Label();
					//Label label = new Label(filter.getPattern());
					return label;
				}

				@Override
				public Node getFirstColumn() {
					//CheckBox ch = new CheckBox("");
					return null;//ch;
				}

				@Override
				public IController getController() {
					return null;
				}
			});
		}
	}

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
	public IController getController() {
		return this;
	}

	@Override
	public Node getControl() throws IOException {
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		return loader.load(getClass().getResourceAsStream("/fxml/settingsPanels/FileFilterSettingPanel.fxml"));
	}

}
