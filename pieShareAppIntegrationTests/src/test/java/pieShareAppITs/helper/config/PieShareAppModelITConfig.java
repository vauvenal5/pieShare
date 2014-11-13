/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.helper.config;

import java.io.File;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.tasks.TestTask;

/**
 *
 * @author Svetoslav
 */
public class PieShareAppModelITConfig extends PieShareAppModel {
	
	@Autowired
	protected PieShareAppServiceConfig service;

	@Bean
	@Lazy
	@Override
	public PieUser pieUser() {
		if (PieShareAppServiceConfig.main) {
			return this.mainPieUser();
		}
		return this.botPieUser();
	}

	@Bean
	@Lazy
	public PieUser mainPieUser() {
		PieUser user = new PieUser();
		PieShareConfiguration config = new PieShareConfiguration();
		config.setPwdFile(new File("testMainKey"));
		config.setTmpDir(new File(ITUtil.getMainTmpDir()));
		config.setWorkingDir(new File(ITUtil.getMainWorkingDir()));
		user.setPieShareConfiguration(config);
		return user;
	}

	@Bean
	@Lazy
	public PieUser botPieUser() {
		PieUser user = new PieUser();
		PieShareConfiguration config = new PieShareConfiguration();
		config.setPwdFile(new File("testBotKey"));
		config.setTmpDir(new File(ITUtil.getBotTmpDir()));
		config.setWorkingDir(new File(ITUtil.getBotWorkingDir()));
		user.setPieShareConfiguration(config);
		return user;
	}
	
	@Bean
	@Lazy
	public TestTask testTask() {
		TestTask task = new TestTask();
		task.setFactory(this.service.testTaskFactory());
		task.setUtil(this.service.itTasksCounter());
		return task;
	}
}
