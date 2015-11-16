package org.pieShare.pieShareAppFx;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class FXMLController {

	private IBeanService beanService;
	private FXMLLoader loader;
	private Stage mainStage;

	public void setBeanService(IBeanService service) {
		this.beanService = service;
	}

	public void setFXMLLoader(FXMLLoader loader) {
		this.loader = loader;
	}

	public void setMainStage(Stage mainStage) {
		try {
			this.mainStage = mainStage;

			Parent root = this.loader.load(getClass().getResourceAsStream("/fxml/MainScene.fxml"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/styles/Styles.css");

			mainStage.setTitle("JavaFX and Maven");
			mainStage.setScene(scene);
			mainStage.show();
		} catch (IOException ex) {
			//todo-sv: error handling
			PieLogger.error(this.getClass(), "Error", ex);
		}
	}

	public Stage getMainStage() {
		return mainStage;
	}
}
