/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.clusterIndependence;

import org.pieShare.pieShareAppFx.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@Configuration
public class PiePlateSpecialConfiguration extends PiePlateConfiguration {

	@Override
	@Bean
	@Lazy
	public CountingChannelTask channelTask() {
		CountingChannelTask task = new CountingChannelTask();
		//the test needs specifically null!!!
		task.setExecutorService(null);
		return task;
	}
}
