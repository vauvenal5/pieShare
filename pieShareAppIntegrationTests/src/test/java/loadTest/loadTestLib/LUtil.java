/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
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

	public static HashMap<String, Integer> getDockerNodes() {
		HashMap<String, Integer> dockerNodes = new HashMap<>();
		dockerNodes.put("http://127.0.0.1:2375", 5);
		//dockerNodes.put("192.168.0.16:2375", 5);
		return dockerNodes;
	}

	public static void runInDockerCluster() {
		runInDockerCluster = true;
	}

	private static boolean runInDockerCluster = false;
	private static boolean dockerError;

	public static boolean startDockerBuild() throws IOException, InterruptedException {

		if (runInDockerCluster) {

			HashMap<String, Integer> dockerNodes = getDockerNodes();
			String dockerTar = LUtil.class.getClassLoader().getResource("docker/loadTest.tar").toString().substring(5);
			ExecutorService exec = Executors.newFixedThreadPool(dockerNodes.size());

			dockerError = false;

			for (Entry<String, Integer> entry : dockerNodes.entrySet()) {
				exec.submit(new Runnable() {
					@Override
					public void run() {
						try {
							String url = entry.getKey() + "/images/vauvenal5/loadtest";
							URL obj = new URL(url);
							HttpURLConnection con = (HttpURLConnection) obj.openConnection();
							con.setRequestMethod("DELETE");
							con.setRequestProperty("force", "true");
							int responseCode = con.getResponseCode();
							con.disconnect();

							url = entry.getKey() + "/build?t=vauvenal5/loadtest&dockerfile=./loadTest/Dockerfile";
							obj = new URL(url);
							con = (HttpURLConnection) obj.openConnection();
							con.setRequestMethod("POST");
							con.setRequestProperty("Content-type", "application/tar");
							con.setDoOutput(true);
							File file = new File(dockerTar);
							FileInputStream fileStr = new FileInputStream(file);
							byte[] b = new byte[(int) file.length()];
							fileStr.read(b);
							con.getOutputStream().write(b);
							con.getOutputStream().flush();
							con.getOutputStream().close();

							responseCode = con.getResponseCode();

							if (responseCode != 200) {
								dockerError = true;
							}
						} catch (MalformedURLException ex) {
							dockerError = true;
						} catch (FileNotFoundException ex) {
							dockerError = true;
						} catch (IOException ex) {
							dockerError = true;
						}
					}
				});
			}

			return dockerError;

		}

		//get the path and substring the 'file:' from the URI
		String dockerfile = LUtil.class.getClassLoader().getResource("docker/loadTest").toString().substring(5);

		ProcessBuilder processBuilder = new ProcessBuilder("docker", "rmi", "-f", "vauvenal5/loadtest");
		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		Process docker = processBuilder.start();

		docker.waitFor();

		processBuilder = new ProcessBuilder("docker", "build", "-t", "vauvenal5/loadtest", dockerfile);
		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		Process proc = processBuilder.start();
		return proc.waitFor() == 0;
	}
	
	private HashMap<String, Integer> startedClusterContainer;

	public boolean startDockerSlave(LoadTestConfigModel ltModel) throws IOException {
		
		if(runInDockerCluster) {
			HashMap<String, Integer> dockerNodes = getDockerNodes();
			
			for (Entry<String, Integer> entry : dockerNodes.entrySet()) {
				if(startedClusterContainer.containsKey(entry) && startedClusterContainer.get(entry) < entry.getValue()) {
					
				}
			}
			
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder("docker", "run", "vauvenal5/loadtest", "slave", String.valueOf(ltModel.getFileCount()));
		Process proc = processBuilder.start();
		return proc.waitFor() == 0;
	}
}
