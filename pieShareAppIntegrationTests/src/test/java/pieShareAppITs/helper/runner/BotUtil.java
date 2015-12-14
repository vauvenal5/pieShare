/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.helper.runner;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pieShareAppITs.helper.ITUtil;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class BotUtil {
	public static AnnotationConfigApplicationContext login(String[] args) throws Exception {
		String cloudName = args[0];
		String password = args[1];
		ITUtil.setUpEnviroment(false);
		AnnotationConfigApplicationContext context = ITUtil.getContext();
		
		ITUtil.executeLoginToTestCloud(context, cloudName, password);
		System.out.println("!loggedIn");
		return context;
	}
}
