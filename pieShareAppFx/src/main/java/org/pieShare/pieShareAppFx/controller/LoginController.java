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
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieShareAppFx.animations.SpinAnimation;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;

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
	private MainSceneController mainSceneController;
	private PieExecutorService executorService;
	private ILoginTask loinTask;
	private IBeanService beanService;

	public void setPieExecutorService(PieExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setLoginTask(ILoginTask loginTask) {
		this.loinTask = loginTask;
	}

	public void setMainSceneController(MainSceneController controller) {
		this.mainSceneController = controller;
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

		loginCommand.setCallback(new ILoginFinished() {

			@Override
			public void error(Exception ex) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						mainSceneController.cloudAvailable(false);
						disableWaitTextField();
						passwordField.getStyleClass().remove("textfieldOK");
						passwordField.getStyleClass().add("textfieldWrong");
					}
				});
			}

			@Override
			public void wrongPassword(WrongPasswordException ex) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						mainSceneController.cloudAvailable(false);
						disableWaitTextField();
						passwordField.getStyleClass().remove("textfieldOK");
						passwordField.getStyleClass().add("textfieldWrong");
					}
				});
			}

			@Override
			public void OK() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						mainSceneController.cloudAvailable(true);
						disableWaitTextField();
						passwordField.getStyleClass().remove("textfieldWrong");
						passwordField.getStyleClass().add("textfieldOK");
						mainSceneController.setClusterSettingControl();
					}
				});
			}
		});

		loinTask.setEvent(loginCommand);
		this.executorService.execute(loinTask);
		labelWaitIcon.setVisible(true);
		labelWaitIcon.setDisable(false);
		animation.start();
	}

	private void disableWaitTextField() {
		labelWaitIcon.setVisible(false);
		labelWaitIcon.setDisable(true);
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mainSceneController.cloudAvailable(false);
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
			mainSceneController.cloudAvailable(true);
			userNameField.setText(user.getCloudName());
			userNameField.disableProperty().set(true);
		}
	}
}
