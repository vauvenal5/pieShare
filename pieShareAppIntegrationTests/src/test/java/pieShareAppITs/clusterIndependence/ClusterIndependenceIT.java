/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.clusterIndependence;

import commonTestTools.TestFileUtils;
import java.io.File;
import java.util.UUID;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileCreatingBot;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ClusterIndependenceIT {
	private AnnotationConfigApplicationContext context;
	private Process process;
	private String cloudName;
	private String password;

	@BeforeClass
	public static void setUpClass() throws Exception {
		ITUtil.setUpEnviroment(true);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		ITUtil.performTearDownDelete();
		context = ITUtil.getContextWithSpecialPiePlateContext(PiePlateSpecialConfiguration.class);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
		ITUtil.performTearDown(context);
	}
	
	/**
	 * This expects that the core will properly handle the state where it can not read
	 * a message which was received. All other exceptions are logged as test errors.
	 * If the message is parsed properly a Nullpointer will be thrown symbolically
	 * showing that the wrong state was reached.
	 * 
	 * Why integration test? The core issue this test is testing is that two 
	 * independent clouds do not cross manipulate each other.
	 * 
	 * Maybe can be removed or has to be changed in future when the 
	 * discovery subtyping is fixed or worked around.
	 * @throws Exception 
	 */
	@Test(timeOut = 60000)
	public void syncOneFileTestExpectedNoNullPointer() throws Exception {		
		PieLogger.info(this.getClass(), "IPv4Prop: {}", System.getProperty("java.net.preferIPv4Stack", "false"));
		CountingChannelTask counter = context.getBean(CountingChannelTask.class);
		PieUser user = context.getBean("pieUser", PieUser.class);
		PieShareConfiguration config = user.getPieShareConfiguration();

		this.cloudName = UUID.randomUUID().toString();
		this.password = UUID.randomUUID().toString();
		
		ITUtil.executeLoginToTestCloud(context, cloudName, password);
		
		//give out discovery time to be set up properly
		//needed?
		//Thread.sleep(5000);

		String cloudNameBot = UUID.randomUUID().toString();
		String passwordBot = UUID.randomUUID().toString();
		System.out.println("Starting bot!");
		this.process = ITUtil.startProcess(FileCreatingBot.class, cloudNameBot, passwordBot);
		ITUtil.waitForProcessToStartup(this.process);
		System.out.println("Bot started!");
		
		System.out.println("Waiting for messages complete!");
		while (counter.getCounter() < 1) {
			Thread.sleep(5000);
		}
		System.out.println("Recived messages!");
		Assert.assertEquals(0, counter.getExceptionCount());
	}
}
