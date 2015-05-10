/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib;

import com.fasterxml.jackson.databind.ObjectMapper;
import commonTestTools.config.PieShareAppServiceTestConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import loadTest.loadTestLib.config.LoadTestConfig;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.service.configurationService.ApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author richy
 */
public class LUtil {
        
        public static String getWorkingDir() {
            return "loadTest/workingDir";
        }
        
        public static String getTmpDir() {
            return "loadTest/tmpDir";
        }
        
        public static String getConfigDir() {
            return "loadTest/loadTestConfig";
        }
    
    	public static AnnotationConfigApplicationContext getContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(PieUtilitiesConfiguration.class);
		context.register(PiePlateConfiguration.class);
		context.register(PieShareAppModel.class);
		context.register(PieShareAppServiceTestConfig.class);
		context.register(PieShareAppTasks.class);
                context.register(LoadTestConfig.class);
		context.refresh();
		return context;
	}
        
        public static void setUpEnviroment() {
		System.setProperty("java.net.preferIPv4Stack", "true");
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
		FileUtils.deleteDirectory(new File(getWorkingDir()));
		FileUtils.deleteDirectory(new File(getTmpDir()));
                FileUtils.deleteDirectory(new File(getConfigDir()));
	}
        
        public static LoadTestConfigModel readJSONConfig() throws IOException {
            InputStream in = LUtil.class.getClassLoader().getResourceAsStream("loadTestConfig.json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(in, LoadTestConfigModel.class);
        }
        
        public static boolean IsMaster()
        {
            String ltType = System.getenv("LTTYPE");
            
            if(ltType == null) {
                ltType = "master";
            }
            
            if(ltType.equals("master")) {
                return true;
            }
            
            return false;
        }
        
        public static Process startDockerBuild() throws IOException {
                //get the path and substring the 'file:' from the URI
                String dockerfile = LUtil.class.getClassLoader().getResource("docker/loadTest").toString().substring(5);

		ProcessBuilder processBuilder = new ProcessBuilder("docker", "build", "-t", "vauvenal5/loadtest", dockerfile);
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		return processBuilder.start();
	}
        
        public static Process startDockerSlave() throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder("docker", "run", "vauvenal5/loadtest", "slave");
                //processBuilder.redirectOutput() // maybe redirect output to file
		return processBuilder.start();
        }
}
