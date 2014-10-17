/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.preferences;

import java.io.InputStream;
import java.net.URL;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.pieShare.pieShareAppFx.controller.BasePreferencesController;
import org.pieShare.pieShareAppFx.preferences.api.IPreferencesEntry;

/**
 *
 * @author Richard
 */
public class BasePreferencesEntry implements IPreferencesEntry {

	private BasePreferencesController basePreferenciesController;

	public void setBasePreferencesController(BasePreferencesController basePreferencesController) {
		this.basePreferenciesController = basePreferencesController;
	}

	@Override
	public Label getTextLabel() {
		Label label = new Label("Base Settings");
		return label;
	}

	@Override
	public Label getIconLabel() {
		InputStream st = getClass().getResourceAsStream("/images/settings_16.png");
		Image image = new Image(st);
		Label label = new Label("", new ImageView(image));
		return label;
	}
}
