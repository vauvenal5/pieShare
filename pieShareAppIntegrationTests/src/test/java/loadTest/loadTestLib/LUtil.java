/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib;

import java.io.File;
import loadTest.loadTestLib.config.LoadTestConfig;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pieShareAppITs.helper.ITUtil;
import static pieShareAppITs.helper.ITUtil.getBotDbDir;
import static pieShareAppITs.helper.ITUtil.getBotKey;
import static pieShareAppITs.helper.ITUtil.getBotTmpDir;
import static pieShareAppITs.helper.ITUtil.getBotWorkingDir;
import static pieShareAppITs.helper.ITUtil.getMainDbDir;
import static pieShareAppITs.helper.ITUtil.getMainKey;
import static pieShareAppITs.helper.ITUtil.getMainTmpDir;
import static pieShareAppITs.helper.ITUtil.getMainWorkingDir;
import pieShareAppITs.helper.config.PieShareAppServiceConfig;

/**
 *
 * @author richy
 */
public class LUtil {
    	public static AnnotationConfigApplicationContext getContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(PieUtilitiesConfiguration.class);
		context.register(PiePlateConfiguration.class);
		context.register(PieShareAppModel.class);
		context.register(PieShareAppServiceConfig.class);
		context.register(PieShareAppTasks.class);
                context.register(LoadTestConfig.class);
		context.refresh();
		return context;
	}
        
        public static void setUpEnviroment() {
		//System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
	}
        
        public static void performTearDown(AnnotationConfigApplicationContext context) throws Exception {
		//shutdown application
		PieShareService service = context.getBean(PieShareService.class);
		service.stop();

		//get dirs to delete
		/*PieShareConfiguration config = context.getBean("pieUser", PieUser.class).getPieShareConfiguration();
		File mainWorkingDir = config.getWorkingDir();//config.getWorkingDirectory();
		File mainTmpDir = config.getTmpDir();
		File configMain = config.getPwdFile();
		config = context.getBean("botPieUser", PieUser.class).getPieShareConfiguration();
		File botWorkingDir = config.getWorkingDir();
		File botTmpDir = config.getTmpDir();
		File configBot = config.getPwdFile();*/
		
		//stop context
		context.close();
		context = null;
	}
        
        public static void performTearDownDelete() throws Exception {
		FileUtils.deleteDirectory(new File(getMainWorkingDir()));
		FileUtils.deleteDirectory(new File(getMainTmpDir()));
		FileUtils.deleteDirectory(new File(getBotWorkingDir()));
		FileUtils.deleteDirectory(new File(getBotTmpDir()));
		FileUtils.deleteDirectory(new File(getMainDbDir()));
		FileUtils.deleteDirectory(new File(getBotDbDir()));
		(new File(getMainKey())).delete();
		(new File(getBotKey())).delete();
	}
}
