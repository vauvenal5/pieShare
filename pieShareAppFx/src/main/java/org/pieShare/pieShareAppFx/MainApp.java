package org.pieShare.pieShareAppFx;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp extends Application {
	
	ApplicationContext context;

	@Override
	public void start(Stage stage) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		context = new ClassPathXmlApplicationContext("pieShareAppFx.xml");
		FXMLController controller = context.getBean(FXMLController.class);
		controller.setMainStage(stage);
	}
	
	@Override
	public void stop() throws Exception {
		PieShareService app = context.getBean(PieShareService.class);
		app.stop();
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
