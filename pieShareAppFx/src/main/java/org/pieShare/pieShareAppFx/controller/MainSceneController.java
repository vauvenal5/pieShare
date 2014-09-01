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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Svetoslav
 */
public class MainSceneController implements Initializable {

	private FXMLLoader loader;
	@FXML
	private AnchorPane mainPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
	
	public void setFXMLLoader(FXMLLoader loader) {
		this.loader = loader;
	}
    
	@FXML
	private void handleAddCloudAction(ActionEvent event) {
		this.mainPane.getChildren().clear();
		try {
			InputStream url = getClass().getResourceAsStream("/fxml/Login.fxml");
			this.mainPane.getChildren().add(this.loader.load(url));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
