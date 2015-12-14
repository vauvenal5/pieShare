/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs.clusterIndependence;

import junit.framework.Assert;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class CountingChannelTask extends ChannelTask {

	private int counter;
	private int exceptionCount;

	public CountingChannelTask() {
		this.counter = 0;
		this.exceptionCount = 0;
	}

	public int getCounter() {
		return counter;
	}

	public int getExceptionCount() {
		return exceptionCount;
	}

	/**
	 * synchronized in this special test scenario!! so we can count properly
	 */
	@Override
	public synchronized void run() {
		this.counter++;
		try {
			super.run();
			return;
		} catch (NullPointerException ex) {
			PieLogger.error(this.getClass(), "Message could be decryptet! This means the cluster did interfear!!", ex);
		} catch (Exception e) {
			PieLogger.error(this.getClass(), "Some unexpected exception occured!", e);
		}
		this.exceptionCount++;
	}
}
