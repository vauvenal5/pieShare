/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorService implements IExecutorService, IShutdownableService {

	private ExecutorService executor;
	private IPieExecutorTaskFactory executorFactory;

	public PieExecutorService() {
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public void setExecutorFactory(IPieExecutorTaskFactory executorFactory) {
		this.executorFactory = executorFactory;
	}

	@Override
	public void execute(IPieTask task) {
		Validate.notNull(task);
		try{
			this.executor.execute(task);
		}
		catch(NullPointerException ex) {
			PieLogger.info(this.getClass(), "Exception in PieExecutorService!", ex);
		}
	}

	@Override
	public void handlePieEvent(IPieEvent event) throws PieExecutorTaskFactoryException {
		IPieEventTask task = this.executorFactory.getTask(event);
		this.execute(task);
	}

	@Override
	public void shutdown() {
		this.executor.shutdown();
	}
}
