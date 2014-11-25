/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.junit.Test;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorServiceTest {

	public PieExecutorServiceTest() {
	}

	/**
	 * Test of execute method, of class PieExecutorService.
	 */
	@Test
	public void testExecute() throws Exception {
		IPieTask task = Mockito.mock(IPieTask.class);
		ExecutorService executor = Mockito.mock(ExecutorService.class);

		PieExecutorService instance = new PieExecutorService();
		instance.setExecutor(executor);
		instance.execute(task);

		Mockito.verify(executor, Mockito.times(1)).execute(task);
	}

	/**
	 * Test of execute method, of class PieExecutorService.
	 */
	@Test(expected = NullPointerException.class)
	public void testExecuteNullValue() throws Exception {
		IPieTask task = null;
		PieExecutorService instance = new PieExecutorService();
		instance.execute(task);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test
	public void testHandlePieEvent() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		IPieEventTask task = Mockito.mock(IPieEventTask.class);
		ExecutorService executor = Mockito.mock(ExecutorService.class);
		IPieExecutorTaskFactory factory = Mockito.mock(IPieExecutorTaskFactory.class);

		Mockito.when(factory.getTask(event)).thenReturn(task);

		PieExecutorService instance = new PieExecutorService();
		instance.setExecutorFactory(factory);
		instance.setExecutor(executor);

		instance.handlePieEvent(event);

		Mockito.verify(executor, Mockito.times(1)).execute(task);
	}
}
