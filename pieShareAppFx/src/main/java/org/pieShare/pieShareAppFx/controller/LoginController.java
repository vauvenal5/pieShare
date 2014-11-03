/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.service.commandService.api.ILoginCommandService;
import org.pieShare.pieShareApp.service.loginService.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.service.loginService.event.LoginFinished;
import org.pieShare.pieShareAppFx.animations.SpinAnimation;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 * FXML Controller class
 *
 * @author Svetoslav
 */
public class LoginController implements Initializable {

	@FXML
	private TextField userNameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Label labelWaitIcon;

	@FXML
	private Label labelWrongPwdInfo;

	private SpinAnimation animation;

	private ILoginCommandService loginCommandService;
	private IBeanService beanService;

	public void setLoginCommandService(LoginCommandService service) {
		this.loginCommandService = service;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@FXML
	private void handleLoginAction(ActionEvent event) {
		PlainTextPassword plainText = new PlainTextPassword();
		plainText.password = this.passwordField.getText().getBytes();
		LoginCommand loginCommand = new LoginCommand();
		loginCommand.setPlainTextPassword(plainText);
		loginCommand.setUserName(this.userNameField.getText());

		loginCommandService.getLoginServiceEventBase().addEventListener(new ILoginFinishedListener() {

			@Override
			public void handleObject(LoginFinished event) {
				animation.stop();

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						labelWaitIcon.setVisible(false);
						labelWaitIcon.setDisable(true);
					}
				});

				switch (event.getState()) {
					case OK:
						break;
					case WrongPassword:
						break;
					case CryptoError:
						break;
				}
			}
		});

		this.loginCommandService.executeCommand(loginCommand);
		labelWaitIcon.setVisible(true);
		labelWaitIcon.setDisable(false);
		animation.start();
	}

	/**
	 * Initializes the controller class.
	 */
	@Override

	public void initialize(URL url, ResourceBundle rb) {
		animation = beanService.getBean(SpinAnimation.class);
		animation.setNode(labelWaitIcon);

		InputStream stDelete = getClass().getResourceAsStream("/images/wait_24.png");
		Image imageDelete = new Image(stDelete);
		labelWaitIcon.setText("");
		labelWaitIcon.setGraphic(new ImageView(imageDelete));
		labelWaitIcon.setVisible(false);
		labelWaitIcon.setDisable(true);

		labelWrongPwdInfo.setVisible(false);
		labelWrongPwdInfo.setDisable(true);

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		if (user.getCloudName() != null) {
			userNameField.setText(user.getCloudName());
			userNameField.disableProperty().set(true);
		}
	}
}
