/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareAppFx.FXMLLoaderExistingController;
import org.pieShare.pieShareAppFx.conrolExtensions.TwoColumnListViewEntry;
import org.pieShare.pieShareAppFx.controller.api.ITwoColumnListViewItem;
import org.pieShare.pieShareAppFx.controller.api.IController;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class TwoColumnListViewController implements IController {

	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@FXML
	private ListView<ITwoColumnListViewItem> listView;

	private ObservableList<ITwoColumnListViewItem> listViewItems;

	@Override
	public Node getControl() throws IOException {
		FXMLLoaderExistingController loader = beanService.getBean(FXMLLoaderExistingController.class);
		return loader.load(getClass().getResourceAsStream("/fxml/TwoColumnListView.fxml"), this);
	}

	public void addItem(ITwoColumnListViewItem item) {
		listViewItems.add(item);
	}

	public void removeItem(ITwoColumnListViewItem item) {
		listViewItems.remove(item);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listViewItems = FXCollections.observableArrayList();
		listView.setItems(listViewItems);

		listView.setCellFactory(new Callback<ListView<ITwoColumnListViewItem>, ListCell<ITwoColumnListViewItem>>() {
			@Override
			public ListCell<ITwoColumnListViewItem> call(final ListView<ITwoColumnListViewItem> param) {
				return new TwoColumnListViewEntry();
			}
		});
	}

	//ToDo: Could solve Database Persist problem.
	public void setOnMouseClicked(EventHandler<? super MouseEvent> value) {
		listView.setOnMouseClicked(value);
	}

	public ITwoColumnListViewItem getSelectedItem() {
		return listView.getSelectionModel().getSelectedItem();
	}
}
