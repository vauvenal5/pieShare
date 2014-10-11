/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorService implements IExecutorService, IShutdownableService {

	private ExecutorService executor;
	private Map<Class, Class> tasks;
	private IBeanService beanService;

	public PieExecutorService() {
	}

	public void setExecutorService(ExecutorService executor) {
		this.executor = executor;
	}

	public void setBeanService(IBeanService service) {
		this.beanService = service;
	}

	public void setMap(Map<Class, Class> map) {
		this.tasks = map;
	}

	@Override
	public void execute(IPieTask task) {
		Validate.notNull(task);
		this.executor.execute(task);
	}

	@Override
	public void handlePieEvent(IPieEvent event) throws PieExecutorServiceException {
		Validate.notNull(event);
		Class taskClass = this.tasks.get(event.getClass());

		try {
			Validate.notNull(taskClass);
		} catch (NullPointerException ex) {
			throw new PieExecutorServiceException("No task registered for given event!", ex);
		}

		IPieEventTask task = null;
		try {
			task = (IPieEventTask) this.beanService.getBean(taskClass);
		} catch (BeanServiceError ex) {
			throw new PieExecutorServiceException("Could not create task!", ex);
		}

		task.setMsg(event);
		this.executor.execute(task);
	}

	@Override
	public <X extends P, P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<X> event, Class<T> task) {
		Validate.notNull(event);
		Validate.notNull(task);

		this.tasks.put(event, task);
	}

	@Override
	public void shutdown() {
		this.executor.shutdown();
	}

}
