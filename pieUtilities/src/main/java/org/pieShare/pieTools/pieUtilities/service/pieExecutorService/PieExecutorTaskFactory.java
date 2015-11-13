/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import javax.inject.Provider;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorTaskFactory implements IPieExecutorTaskFactory {
	
	private Map<Class, Provider> providers;

	public void setTasks(Map tasks) {
		this.providers = tasks;
	}
	
	@Override
	public IPieEventTask getTask(IPieEvent event) throws PieExecutorTaskFactoryException {
		Validate.notNull(event);
		Provider provider = this.providers.get(event.getClass());

		try {
			Validate.notNull(provider);
		} catch (NullPointerException ex) {
			PieLogger.info(this.getClass(), "No task registered for given event: {}", event.getClass(), ex);
			throw new PieExecutorTaskFactoryException("No task registered for given event!", ex);
		}

		IPieEventTask task = (IPieEventTask) provider.get();
		
		try {
			Validate.notNull(task);
		} catch (NullPointerException ex) {
			PieLogger.info(this.getClass(), "Provider did not return task instance for: {}", event.getClass(), ex);
			throw new PieExecutorTaskFactoryException("Provider did not return task instance!", ex);
		}

		task.setEvent(event);
		return task;
	}

	@Override
	public <P extends IPieEvent> void removeTaskRegistration(Class<P> event) {
		this.providers.remove(event);
	}

	@Override
	public <X extends P, P extends IPieEvent, T extends IPieEventTask<P>> void registerTaskProvider(Class<X> event, Provider<T> provider) {
		Validate.notNull(event);
		Validate.notNull(provider);

		this.providers.put(event, provider);
	}
}
