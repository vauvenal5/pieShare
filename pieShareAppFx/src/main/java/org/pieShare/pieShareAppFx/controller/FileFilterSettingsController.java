/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.pieShare.pieShareApp.service.fileFilterService.FileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFilter;
import org.pieShare.pieShareAppFx.conrolExtensions.TwoColumnListView;
import org.pieShare.pieShareAppFx.conrolExtensions.api.ITwoColumnListView;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.regexService.IRegexService;

/**
 *
 * @author Richard
 */
public class FileFilterSettingsController implements Initializable {

	private boolean isCorrectRegex = false;
	private IRegexService regexService;
	private IFileFilterService fileFilterService;
	private IBeanService beanService;
	@FXML
	private ListView<ITwoColumnListView> listViewFilters;

	@FXML
	private TextField patternTextField;

	private ObservableList<ITwoColumnListView> listItems;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setRegexService(IRegexService regexService) {
		this.regexService = regexService;
	}

	public void setFileFilterService(IFileFilterService fileFilterService) {
		this.fileFilterService = fileFilterService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listItems = FXCollections.observableArrayList();
		listViewFilters.setItems(listItems);

		listViewFilters.setCellFactory(new Callback<ListView<ITwoColumnListView>, ListCell<ITwoColumnListView>>() {
			@Override
			public ListCell<ITwoColumnListView> call(final ListView<ITwoColumnListView> param) {
				return new TwoColumnListView();
			}
		});

		listViewFilters.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (listViewFilters.getSelectionModel().getSelectedItems() != null) {
					patternTextField.setText(((IFilter) listViewFilters.getSelectionModel().getSelectedItem().getObject()).getPattern());
				}
			}
		});
		
		refreshList();

		patternTextField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String patt = patternTextField.getText();

				try {
					regexService.setPattern(patt);
				} catch (Exception ex) {
					isCorrectRegex = false;
					patternTextField.setStyle("-fx-background-color: red");
					return;
				}
				patternTextField.setStyle("-fx-background-color: lime");
				isCorrectRegex = true;
			}
		});

	}

	@FXML
	private void handleAddAction(ActionEvent event) {
		String text = patternTextField.getText();
		IFilter fil = beanService.getBean(FileFilter.class);
		fil.setPattern(text);
		fileFilterService.addFilter(fil);
		refreshList();
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
			listItems.add(new ITwoColumnListView() {

				@Override
				public Object getObject() {
					return filter; //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public Label getSecondColumn() {
					Label label = new Label(filter.getPattern());
					return label;
				}

				@Override
				public Node getFirstColumn() {
					//CheckBox ch = new CheckBox("");
					return null;//ch;
				}

				@Override
				public String getPanelPath() {
					return null;
				}
			});
		}
	}

}
