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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareAppFx.animations.SpinAnimation;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class WorkingMessageController implements Initializable {

	private SpinAnimation spinAnimation;
	private IBeanService beanService;

	@FXML
	private Label labelImage;

	@FXML
	private Label labelMessage;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public Node getControl() throws IOException {
		InputStream st = getClass().getResourceAsStream("/fxml/messagePanels/WorkingMessage.fxml");
		FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
		return loader.load(st);
	}

	public void setMessage(String message) {
		labelMessage.setText(message);
	}

	public void startAnimation() {
		spinAnimation.start();
	}

	public void stopAnimation() {
		spinAnimation.stop();
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		spinAnimation = beanService.getBean(SpinAnimation.class);

		InputStream stDelete = getClass().getResourceAsStream("/images/wait_24.png");
		Image imageDelete = new Image(stDelete);
		labelImage.setText("");
		labelImage.setGraphic(new ImageView(imageDelete));

		spinAnimation.setNode(labelImage);
	}
}
