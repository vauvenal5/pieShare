/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.runner;

import pieShareAppITs.helper.config.PieShareAppServiceConfig;
import pieShareAppITs.helper.ITUtil;
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
		ITUtil.setUpEnviroment(false);
		AnnotationConfigApplicationContext context = ITUtil.getContext();
		
		ITUtil.executeLoginToTestCloud(context);
		System.out.println("!loggedIn");
	}
	
}
