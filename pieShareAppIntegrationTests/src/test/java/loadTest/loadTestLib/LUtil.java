/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.service.PieShareService;
import org.springframework.context.ApplicationContext;

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

    /*public static AnnotationConfigApplicationContext getContext() {
     AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
     context.register(PieUtilitiesConfiguration.class);
     context.register(PiePlateConfiguration.class);
     context.register(PieShareAppModel.class);
     context.register(PieShareAppServiceTestConfig.class);
     context.register(PieShareAppTasks.class);
     context.register(LoadTestConfig.class);
     context.refresh();
     return context;
     }*/
    public static void setUpEnviroment() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
    }

    public static void performTearDown(ApplicationContext context) throws Exception {

        //shutdown application
        PieShareService service = context.getBean(PieShareService.class);
        service.stop();

        System.out.println("Services stoped!");

		//stop context
		/*context.close();
         context = null;
		
         System.out.println("Context closed!");*/
    }

    public static void performTearDown(ApplicationContext context, List<Process> slaves) throws Exception {
        performTearDown(context);
        slaves.forEach(s -> s.destroy());
    }

    public static void performTearDownDelete() throws Exception {
        FileUtils.deleteDirectory(new File(getWorkingDir()));
        FileUtils.deleteDirectory(new File(getTmpDir()));
        FileUtils.deleteDirectory(new File(getConfigDir()));
    }

    public static List<LoadTestConfigModel> readJSONConfig() throws IOException {
        InputStream in = LUtil.class.getClassLoader().getResourceAsStream("loadTestConfig.json");
        ObjectMapper mapper = new ObjectMapper();
        List<LoadTestConfigModel> myObjects = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, LoadTestConfigModel.class));
        return myObjects;
    }

    public static void setUpResultFile() throws IOException {
        FileWriter writer = new FileWriter("loadTestResults.csv");
        writer.write("NodeCount,FileCount,FileSize,ResultTime\n");
        writer.flush();
        writer.close();
    }

    public static void writeCSVResult(LoadTestConfigModel model, long resTime) throws IOException {
        FileWriter writer = new FileWriter("loadTestResults.csv", true);
        writer.append(String.valueOf(model.getNodeCount()));
        writer.append(",");
        writer.append(String.valueOf(model.getFileCount()));
        writer.append(",");
        writer.append(String.valueOf(model.getFileSize()));
        writer.append(",");
        writer.append(String.valueOf(resTime));
        writer.append("\n");
        writer.flush();
        writer.close();
    }

    public static boolean IsMaster() {
        String ltType = System.getenv("LTTYPE");

        if (ltType == null) {
            return true;
        }

        if (ltType.equals("master")) {
            return true;
        }

        return false;
    }

    public static Process startDockerBuild() throws IOException, InterruptedException {
        //get the path and substring the 'file:' from the URI
        String dockerfile = LUtil.class.getClassLoader().getResource("docker/loadTest").toString().substring(5);

        ProcessBuilder processBuilder = new ProcessBuilder("docker", "rmi", "-f", "vauvenal5/loadtest");
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process docker = processBuilder.start();

        docker.waitFor();

        processBuilder = new ProcessBuilder("docker", "build", "-t", "vauvenal5/loadtest", dockerfile);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return processBuilder.start();
    }

    public static Process startDockerSlave(LoadTestConfigModel ltModel) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("docker", "run", "vauvenal5/loadtest", "slave", String.valueOf(ltModel.getFileCount()));
        return processBuilder.start();
    }
}
