/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx;

import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.pieShare.pieShareAppFx.controller.api.IController;

/**
 *
 * @author Richard
 */
public class FXMLLoaderExistingController extends FXMLLoader {

	private IController controller;

	public FXMLLoaderExistingController() {
		this.setControllerFactory(new Callback<Class<?>, Object>() {

			@Override
			public Object call(Class<?> param) {
				return controller;
			}
		});
	}

	public <T> T load(InputStream stream, IController controller) throws IOException {
		this.controller = controller;
		return this.load(stream);
	}
}
