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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.command.ResetPwdCommand;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.api.IResetPwdCalback;
import org.pieShare.pieShareAppFx.animations.SpinAnimation;
import org.pieShare.pieShareAppFx.controller.api.IController;
import org.pieShare.pieShareAppFx.events.LoginStateChangedEvent;
import org.pieShare.pieShareAppFx.events.api.ILoginStateChangedListener;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * FXML Controller class
 */
public class LoginController implements IController {

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordFieldRepeat;

    @FXML
    private Label labelWaitIcon;

    @FXML
    private Hyperlink resetPasswordLink;

    @FXML
    private BorderPane additionalOptionsPane;

    private SpinAnimation animation;
    private PieExecutorService executorService;
    private ILoginTask loinTask;
    private IBeanService beanService;
    private IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> loginStateChangedEvent;
    private BasePreferencesController basePreferencesController;
	private IUserService userService;
	
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

    public void setBasePreferencesController(BasePreferencesController basePreferencesController) {
	this.basePreferencesController = basePreferencesController;
    }

    public void setLoginStateChangedEventBase(IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> loginStateChangedEvent) {
	this.loginStateChangedEvent = loginStateChangedEvent;
    }

    public IEventBase<ILoginStateChangedListener, LoginStateChangedEvent> getLoginStateChangedEventBase() {
	return this.loginStateChangedEvent;
    }

    public void setPieExecutorService(PieExecutorService executorService) {
	this.executorService = executorService;
    }

    public void setLoginTask(ILoginTask loginTask) {
	this.loinTask = loginTask;
    }

    public void setBeanService(IBeanService beanService) {
	this.beanService = beanService;
    }

    @FXML
    private void handlePasswordResetLink(ActionEvent event) {
	ResetPwdCommand command = new ResetPwdCommand();
	command.setCallback(new IResetPwdCalback() {
	    @Override
	    public void pwdResetOK() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			passwordFieldRepeat.setVisible(true);
			passwordField.clear();
			disableWaitTextField();

			passwordField.getStyleClass().remove("textfieldWrong");
			passwordField.getStyleClass().remove("textfieldOK");
		    }
		});
	    }
	});

	enableWaitTextField();
	animation.start();

	try {
	    executorService.handlePieEvent(command);
	}
	catch (PieExecutorTaskFactoryException ex) {
	    PieLogger.error(this.getClass(), "Error executing password reset task", ex);
	}
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {

	PieUser user = userService.getUser();

	if (!user.hasPasswordFile()) {
	    if (!passwordField.getText().equals(passwordFieldRepeat.getText())) {
		passwordField.getStyleClass().remove("textfieldOK");
		passwordField.getStyleClass().add("textfieldWrong");
		passwordFieldRepeat.getStyleClass().remove("textfieldOK");
		passwordFieldRepeat.getStyleClass().add("textfieldWrong");
		return;
	    }
	}

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
			disableWaitTextField();
			passwordField.getStyleClass().remove("textfieldWrong");
			passwordField.getStyleClass().add("textfieldOK");
			loginStateChangedEvent.fireEvent(new LoginStateChangedEvent(this, true));
		    }
		});
	    }
	});

	loinTask.setEvent(loginCommand);
	enableWaitTextField();
	animation.start();

	try {
	    executorService.handlePieEvent(loginCommand);
	}
	catch (PieExecutorTaskFactoryException ex) {
	    PieLogger.error(this.getClass(), "Error executong login event", ex);
	}
    }

    private void disableWaitTextField() {
	labelWaitIcon.setVisible(false);
	labelWaitIcon.setDisable(true);
    }

    private void enableWaitTextField() {
	labelWaitIcon.setVisible(true);
	labelWaitIcon.setDisable(false);
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

	PieUser user = userService.getUser();
	if (user.getCloudName() != null) {
	    userNameField.setText(user.getCloudName());
	    userNameField.disableProperty().set(true);
	}

	if (user.hasPasswordFile()) {
	    passwordFieldRepeat.setVisible(false);
	    additionalOptionsPane.getChildren().clear();
	}
	else {
	    try {
		additionalOptionsPane.setCenter(basePreferencesController.getControl());
	    }
	    catch (IOException ex) {
		PieLogger.error(this.getClass(), "Error setting BasePreferences Control", ex);
	    }
	}
    }

    @Override
    public Node getControl() throws IOException {
	FXMLLoader loader = beanService.getBean(PieShareAppBeanNames.getGUILoader());
	InputStream st = getClass().getResourceAsStream("/fxml/Login.fxml");
	return loader.load(st);
    }
}
