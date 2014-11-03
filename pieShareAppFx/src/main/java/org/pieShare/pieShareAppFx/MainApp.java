package org.pieShare.pieShareAppFx;

import ch.qos.logback.classic.LoggerContext;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareAppFx;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp extends Application {
	
	AnnotationConfigApplicationContext context;

	@Override
	public void start(Stage stage) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		context = new AnnotationConfigApplicationContext();
                context.register(PieUtilitiesConfiguration.class);
                context.register(PiePlateConfiguration.class);
                context.register(PieShareAppModel.class);
                context.register(PieShareAppService.class);
                context.register(PieShareAppTasks.class);
                context.register(PieShareAppFx.class);
                context.refresh();
		context.registerShutdownHook();
		FXMLController controller = context.getBean(FXMLController.class);
		controller.setMainStage(stage);
	}
	
	@Override
	public void stop() throws Exception {
		PieShareService app = context.getBean(PieShareService.class);
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.stop();
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
