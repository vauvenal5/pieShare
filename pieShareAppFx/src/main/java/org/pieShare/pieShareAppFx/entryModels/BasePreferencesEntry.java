/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.entryModels;

import java.io.InputStream;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.pieShare.pieShareAppFx.controller.BasePreferencesController;
import org.pieShare.pieShareAppFx.conrolExtensions.api.IIconNameEntry;

/**
 *
 * @author Richard
 */
public class BasePreferencesEntry implements IIconNameEntry {

	private BasePreferencesController basePreferenciesController;

	public void setBasePreferencesController(BasePreferencesController basePreferencesController) {
		this.basePreferenciesController = basePreferencesController;
	}

	@Override
	public Label getSecondColumn() {
		Label label = new Label("Base Settings");
		return label;
	}

	@Override
	public Label getFirstColumn() {
		InputStream st = getClass().getResourceAsStream("/images/settings_16.png");
		Image image = new Image(st);
		Label label = new Label("", new ImageView(image));
		return label;
	}

	@Override
	public String getPanelPath() {
		return "/fxml/settingsPanels/BasePreferencesPanel.fxml";
	}
}
