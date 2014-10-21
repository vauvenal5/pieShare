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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFilter;
import org.pieShare.pieShareAppFx.conrolExtensions.IconNameListViewItem;
import org.pieShare.pieShareAppFx.conrolExtensions.api.IIconNameEntry;
import org.pieShare.pieTools.pieUtilities.service.regexService.IRegexService;

/**
 *
 * @author Richard
 */
public class FileFilterSettingsController implements Initializable {

	private IRegexService regexService;
	private IFileFilterService fileFilterService;

	@FXML
	private ListView<IIconNameEntry> listViewFilters;
	private ObservableList<IIconNameEntry> listItems;

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

		listViewFilters.setCellFactory(new Callback<ListView<IIconNameEntry>, ListCell<IIconNameEntry>>() {
			@Override
			public ListCell<IIconNameEntry> call(final ListView<IIconNameEntry> param) {
				return new IconNameListViewItem();
			}
		});
		
		for (IFilter filter : fileFilterService.getAllFilters()) {
			listItems.add(new IIconNameEntry() {

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
					CheckBox ch = new CheckBox("");
					return ch;
				}

				@Override
				public String getPanelPath() {
					return null;
				}
			});
		}

	}

	@FXML
	private void handleAddAction(ActionEvent event) {

	}

	@FXML
	private void handleClearTextAction(ActionEvent event) {

	}

	@FXML
	private void handleDeleteAction(ActionEvent event) {

	}

}
