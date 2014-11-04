/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.runner;

import pieShareAppITs.helper.config.PieShareAppServiceConfig;
import pieShareAppITs.helper.ITUtil;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav
 */
public class FileSyncMain {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		
		PieShareAppServiceConfig.main = false;
		AnnotationConfigApplicationContext context = ITUtil.getContext();
		
		ITUtil.executeLoginToTestCloud(context);
	}
	
}
