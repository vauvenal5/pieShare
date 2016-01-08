/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieshare.piespring.service.database.DatabaseService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITUtil;

/**
 *
 * @author Svetoslav
 */
public class DataBaseIT {
	
	private AnnotationConfigApplicationContext context;
	
	public DataBaseIT() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
            ITUtil.performTearDownDelete();
            ITUtil.setUpEnviroment(true);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		context = ITUtil.getContext();
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		ITUtil.performTearDown(context);
	}
	
	@Test
	public void persistPieFile() {
		DatabaseService dbService = this.context.getBean(DatabaseService.class);
		
		PieFile file = new PieFile();
		file.setDeleted(false);
		file.setName("testFile");
		file.setLastModified(0);
		file.setMd5("test".getBytes());
		file.setRelativePath("testFolder");
		
		dbService.mergePieFile(file);
		file.setName("testFile2");
		dbService.mergePieFile(file);
		
		PieFile fileFromDB = dbService.findPieFile(file);

		Assert.assertEquals(file, fileFromDB);
	}
        
        @Test
        public void persistPieFolder() {
            DatabaseService dbService = this.context.getBean(DatabaseService.class);
            
            PieFolder folder = new PieFolder();
            folder.setName("testFolder");
            folder.setRelativePath("testFolder");
            dbService.persistPieFolder(folder);
            
            folder.setName("testFolder2");
            folder.setRelativePath("testFolder2");
            dbService.mergePieFolder(folder);
            
            PieFolder folderFromDB = dbService.findPieFolder(folder);
            
            Assert.assertEquals(folder.getRelativePath(), folderFromDB.getRelativePath());
            
            try{
                dbService.removePieFolder(folder);
            } catch (Exception e) {
                System.out.println(e);
            }
            
            //Assert.assertNull(dbService.findPieFolder(folder));
        }
}
