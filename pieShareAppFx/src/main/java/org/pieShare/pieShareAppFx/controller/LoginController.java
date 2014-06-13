/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
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

	private LoginCommandService loginCommandService;
	private IBeanService beanService;

	public void setLoginCommandService(LoginCommandService service) {
		this.loginCommandService = service;
	}

	@FXML
	private void handleLoginAction(ActionEvent event) {
		PlainTextPassword plainText = new PlainTextPassword();
		plainText.password = this.passwordField.getText().toCharArray();
		LoginCommand loginCommand = new LoginCommand();
		loginCommand.setPlainTextPassword(plainText);
		loginCommand.setUserName(this.userNameField.getText());
		this.loginCommandService.executeCommand(loginCommand);
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
	}

}
