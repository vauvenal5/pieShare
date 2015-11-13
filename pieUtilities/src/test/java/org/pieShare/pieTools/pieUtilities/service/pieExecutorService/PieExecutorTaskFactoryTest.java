/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import org.testng.annotations.Test;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
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
		Map<Class, Provider> map = Mockito.mock(Map.class);
		final IPieEventTask task = Mockito.mock(IPieEventTask.class);
		IBeanService beanService = Mockito.mock(IBeanService.class);

		Mockito.when(map.get(event.getClass())).thenReturn(new Provider<IPieEventTask>() {
			@Override
			public IPieEventTask get() {
				return task;
			}
		});
		Class clazz = task.getClass();
		Mockito.when(beanService.getBean(clazz)).thenReturn(task);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);

		IPieEventTask res = instance.getTask(event);

		Mockito.verify(task, Mockito.times(1)).setEvent(event);
		Assert.assertEquals(res, task);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = PieExecutorTaskFactoryException.class)
	public void testHandlePieEventTaskNotCreated() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		Map<Class, Provider> map = Mockito.mock(Map.class);
		IBeanService beanService = Mockito.mock(IBeanService.class);

		Mockito.when(map.get(event.getClass())).thenReturn(new Provider<IPieEventTask>() {
			@Override
			public IPieEventTask get() {
				return null;
			}
		});
		Mockito.when(beanService.getBean(IPieEventTask.class)).thenThrow(BeanServiceError.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);

		instance.getTask(event);
	}

	/**
	 * Test of handlePieEvent method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = PieExecutorTaskFactoryException.class)
	public void testHandlePieEventNoTaskRegistered() throws Exception {
		IPieEvent event = Mockito.mock(IPieEvent.class);
		Map<Class, Provider> map = Mockito.mock(Map.class);

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
		Map<Class, Provider> map = Mockito.mock(Map.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);
		
		Provider<IPieEventTask> provider = new Provider<IPieEventTask>() {

			@Override
			public IPieEventTask get() {
				return Mockito.mock(IPieEventTask.class);
			}
		};
		
		instance.registerTaskProvider(IPieEvent.class, provider);

		Mockito.verify(map, Mockito.times(1)).put(IPieEvent.class, provider);
	}

	/**
	 * Test of registerTask method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testRegisterTaskEventNullValue() {
		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.registerTaskProvider(null, Mockito.mock(Provider.class));
	}

	/**
	 * Test of registerTask method, of class PieExecutorService.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testRegisterTaskTaskNullValue() {
		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.registerTaskProvider(IPieEvent.class, null);
	}

	@Test
	public void testRegisterExtendedTask() {
		Map<Class, Provider> map = Mockito.mock(Map.class);

		PieExecutorTaskFactory instance = new PieExecutorTaskFactory();
		instance.setTasks(map);

		class SubEvent implements IPieEvent {
		}

		class SubSubEvent extends SubEvent {
		}

		class SubTask implements IPieEventTask<SubEvent> {

			@Override
			public void setEvent(SubEvent msg) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

			@Override
			public void run() {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

		}
		
		Provider provider = new Provider<SubTask>() {

			@Override
			public SubTask get() {
				return Mockito.mock(SubTask.class);
			}
		};

		instance.registerTaskProvider(SubSubEvent.class, provider);

		Mockito.verify(map, Mockito.times(1)).put(SubSubEvent.class, provider);
	}
}
