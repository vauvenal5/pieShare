/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests;

import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav
 */
public class IntegrationTestUtil {
	
	private int fileTransferCompletedCounter;
	
	public IntegrationTestUtil() {
		this.fileTransferCompletedCounter = 0;
	}
	
	void fileTransferCompletedTaskCompleted() {
		this.fileTransferCompletedCounter++;
	}
	
	int getFileTransferCompletedTask() {
		return this.fileTransferCompletedCounter;
	}
	
	public static AnnotationConfigApplicationContext getContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(PieUtilitiesConfiguration.class);
		context.register(PiePlateConfiguration.class);
		context.register(PieShareAppModel.class);
		context.register(PieShareAppServiceConfig.class);
		context.register(PieShareAppTasks.class);
		context.refresh();
		return context;
	}
}
