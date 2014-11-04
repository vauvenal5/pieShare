/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs;

import pieShareAppITs.helper.ITFileUtils;
import pieShareAppITs.helper.ITUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author Svetoslav
 */
public class DeleteIT {
	
	private AnnotationConfigApplicationContext context;
	private Process process;
	private List<File> files;
	
	public DeleteIT() {
		files = new ArrayList<>();
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		ITUtil.setUpEnviroment(true);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		//create files
		for(int i = 0; i< 10; i++) {
			//this has to be done for both folder with the same files!!!
			files.add(ITFileUtils.createFile(new File("workingDirTestMain", String.valueOf(i)), 2048));
		}
		
		context = ITUtil.getContext();
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
		files.clear();
		ITUtil.performTearDown(context);
	}
}
