/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests.helper.runner;

import integrationTests.helper.config.PieShareAppServiceConfig;
import integrationTests.helper.ITUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav
 */
public class FileSyncMain {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		
		PieShareAppServiceConfig.main = false;
		AnnotationConfigApplicationContext context = ITUtil.getContext();
		
		ITUtil.executeLoginToTestCloud(context);
	}
	
}
