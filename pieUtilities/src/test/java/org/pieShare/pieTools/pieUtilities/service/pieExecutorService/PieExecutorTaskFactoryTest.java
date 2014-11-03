/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.testng.annotations.Test;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.testng.Assert;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorTaskFactoryTest {
	
	public PieExecutorTaskFactoryTest() {
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test
	public void testHandlePieEvent() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		Map<Class, Class> map = Mockito.mock(Map.class);
		IPieEventTask task = Mockito.mock(IPieEventTask.class);
		IBeanService beanService = Mockito.mock(IBeanService.class);

		Mockito.when(map.get(event.getClass())).thenReturn(task.getClass());
		Class clazz = task.getClass();
		Mockito.when(beanService.getBean(clazz)).thenReturn(task);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setBeanService(beanService);
		instance.setTasks(map);

		IPieEventTask res = instance.getTask(event);

		Mockito.verify(task, Mockito.times(1)).setMsg(event);
		Assert.assertEquals(res, task);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = PieExecutorTaskFactoryException.class)
	public void testHandlePieEventTaskNotCreated() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		Map<Class, Class> map = Mockito.mock(Map.class);
		IBeanService beanService = Mockito.mock(IBeanService.class);

		Mockito.when(map.get(event.getClass())).thenReturn(IPieEventTask.class);
		Mockito.when(beanService.getBean(IPieEventTask.class)).thenThrow(BeanServiceError.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setBeanService(beanService);
		instance.setTasks(map);

		instance.getTask(event);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = PieExecutorTaskFactoryException.class)
	public void testHandlePieEventNoTaskRegistered() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		Map<Class, Class> map = Mockito.mock(Map.class);

		Mockito.when(map.get(event.getClass())).thenReturn(null);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);
		instance.getTask(event);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testHandlePieEventNullValue() throws Exception {
		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.getTask(null);
	}

	/**
	 * Test of registerTask method, of class PieExecutorService.
	 */
	@Test
	public void testRegisterTask() {
		Map<Class, Class> map = Mockito.mock(Map.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);
		instance.registerTask(IPieEvent.class, IPieEventTask.class);

		Mockito.verify(map, Mockito.times(1)).put(IPieEvent.class, IPieEventTask.class);
	}

	/**
	 * Test of registerTask method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testRegisterTaskEventNullValue() {
		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.registerTask(null, IPieEventTask.class);
	}

	/**
	 * Test of registerTask method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testRegisterTaskTaskNullValue() {
		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.registerTask(IPieEvent.class, null);
	}

	@Test
	public void testRegisterExtendedTask() {
		Map<Class, Class> map = Mockito.mock(Map.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);

		class SubEvent implements IPieEvent {
		}

		class SubSubEvent extends SubEvent {
		}

		class SubTask implements IPieEventTask<SubEvent> {

			@Override
			public void setMsg(SubEvent msg) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

			@Override
			public void run() {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

		}

		instance.registerTask(SubSubEvent.class, SubTask.class);

		Mockito.verify(map, Mockito.times(1)).put(SubSubEvent.class, SubTask.class);
	}
}
