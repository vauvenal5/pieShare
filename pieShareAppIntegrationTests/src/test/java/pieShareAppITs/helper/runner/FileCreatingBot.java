/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.helper.runner;

import commonTestTools.TestFileUtils;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class FileCreatingBot {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = BotUtil.login(args);
		
		PieUser user = context.getBean("pieUser", PieUser.class);
		PieShareConfiguration config = user.getPieShareConfiguration();
		
		File filex = new File(config.getWorkingDir().getParent(), "test.txt");
                TestFileUtils.createFile(filex, 2);
		File fileMain = new File(config.getWorkingDir(), "test.txt");
		
		FileUtils.moveFile(filex, fileMain);
	}
	
}
