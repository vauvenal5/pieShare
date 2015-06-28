/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.config;

import loadTest.loadTestLib.helper.LFileComparer;
import loadTest.loadTestLib.message.AllClientsDoneMessage;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.message.ClientIsUpMessage;
import loadTest.loadTestLib.message.MasterIsReadyMessage;
import loadTest.loadTestLib.task.AllClientsDoneTask;
import loadTest.loadTestLib.task.AllFilesCompleteTask;
import loadTest.loadTestLib.task.ClientIsUpTask;
import loadTest.loadTestLib.task.MasterIsReadyTask;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
@Configuration
public class LoadTestConfig {

	@Autowired
	private PieShareAppService pieShareAppService;

	@Bean
	@Scope(value = "prototype")
	public AllFilesCompleteMessage allFilesCompleteMessage() {
		AllFilesCompleteMessage message = new AllFilesCompleteMessage();
		message.setAddress(new JGroupsPieAddress());
		return message;
	}

	@Bean
	@Scope(value = "prototype")
	public ClientIsUpMessage clientIsUpMessage() {
		ClientIsUpMessage message = new ClientIsUpMessage();
		message.setAddress(new JGroupsPieAddress());
		return message;
	}

	@Bean
	@Scope(value = "prototype")
	public AllClientsDoneMessage allClientsDoneMessage() {
		AllClientsDoneMessage message = new AllClientsDoneMessage();
		message.setAddress(new JGroupsPieAddress());
		return message;
	}
	
	@Bean
	@Scope(value = "prototype")
	public MasterIsReadyMessage masterIsReadyMessage() {
		MasterIsReadyMessage message = new MasterIsReadyMessage();
		message.setAddress(new JGroupsPieAddress());
		return message;
	}

	@Bean
	@Scope(value = "prototype")
	public AllFilesCompleteTask allFilesCompleteTask() {
		AllFilesCompleteTask task = new AllFilesCompleteTask();
		task.setTaskCounter(iTTasksCounter());
		task.setComparer(ltFileComparer());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public ClientIsUpTask clientIsUpTask() {
		ClientIsUpTask task = new ClientIsUpTask();
		task.setTaskCounter(iTTasksCounter());
		return task;
	}

	@Bean
	@Scope(value = "prototype")
	public AllClientsDoneTask allClientsDoneTask() {
		AllClientsDoneTask task = new AllClientsDoneTask();
		task.setTaskCounter(iTTasksCounter());
		return task;
	}
	
	@Bean
	@Scope(value = "prototype")
	public MasterIsReadyTask masterIsReadyTask() {
		MasterIsReadyTask task = new MasterIsReadyTask();
		task.setTaskCounter(iTTasksCounter());
		return task;
	}

	@Bean
	@Lazy
	public ITTasksCounter iTTasksCounter() {
		return new ITTasksCounter();
	}

	@Bean
	@Lazy
	public LFileComparer ltFileComparer() {
		LFileComparer com = new LFileComparer();
		com.setFileCompareService(pieShareAppService.fileCompareService());
		return com;
	}
}
