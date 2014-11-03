/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorTaskFactory implements IPieExecutorTaskFactory {
	
	private Map<Class, Class> tasks;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setTasks(Map<Class, Class> tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public IPieEventTask getTask(IPieEvent event) throws PieExecutorTaskFactoryException {
		Validate.notNull(event);
		Class taskClass = this.tasks.get(event.getClass());

		try {
			Validate.notNull(taskClass);
		} catch (NullPointerException ex) {
			PieLogger.info(this.getClass(), "No task registered for given event: {}", event.getClass(), ex);
			throw new PieExecutorTaskFactoryException("No task registered for given event!", ex);
		}

		IPieEventTask task = null;
		try {
			task = (IPieEventTask) this.beanService.getBean(taskClass);
		} catch (BeanServiceError ex) {
			throw new PieExecutorTaskFactoryException("Could not create task!", ex);
		}

		task.setMsg(event);
		return task;
	}
	
	@Override
	public <X extends P, P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<X> event, Class<T> task) {
		Validate.notNull(event);
		Validate.notNull(task);

		this.tasks.put(event, task);
	}

	@Override
	public <P extends IPieEvent> void removeTaskRegistration(Class<P> event) {
		this.tasks.remove(event);
	}
}
