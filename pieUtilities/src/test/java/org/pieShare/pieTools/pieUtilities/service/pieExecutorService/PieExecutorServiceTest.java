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
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;

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
        instance.setExecutorService(executor);
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
        Map<Class, Class> map = Mockito.mock(Map.class);
        IPieEventTask task = Mockito.mock(IPieEventTask.class);
        IBeanService beanService = Mockito.mock(IBeanService.class);
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        
        Mockito.when(map.get(event.getClass())).thenReturn(task.getClass());
        Class clazz = task.getClass();
        Mockito.when(beanService.getBean(clazz)).thenReturn(task);
        
        PieExecutorService instance = new PieExecutorService();
        instance.setBeanService(beanService);
        instance.setMap(map);
        instance.setExecutorService(executor);
        
        instance.handlePieEvent(event);
        
        Mockito.verify(task, Mockito.times(1)).setMsg(event);
        Mockito.verify(executor, Mockito.times(1)).execute(task);
    }
    
    /**
     * Test of handlePieEvent method, of class PieExecutorService.
     */
    @Test(expected = PieExecutorServiceException.class)
    public void testHandlePieEventTaskNotCreated() throws Exception {
        IPieEvent event = Mockito.mock(IPieEvent.class);
        Map<Class, Class> map = Mockito.mock(Map.class);
        IBeanService beanService = Mockito.mock(IBeanService.class);
        
        Mockito.when(map.get(event.getClass())).thenReturn(IPieEventTask.class);
        Mockito.when(beanService.getBean(IPieEventTask.class)).thenThrow(BeanServiceError.class);
        
        PieExecutorService instance = new PieExecutorService();
        instance.setBeanService(beanService);
        instance.setMap(map);
        
        instance.handlePieEvent(event);
    }
    
    /**
     * Test of handlePieEvent method, of class PieExecutorService.
     */
    @Test(expected = PieExecutorServiceException.class)
    public void testHandlePieEventNoTaskRegistered() throws Exception {
        IPieEvent event = Mockito.mock(IPieEvent.class);
        Map<Class, Class> map = Mockito.mock(Map.class);
        
        Mockito.when(map.get(event.getClass())).thenReturn(null);
        
        PieExecutorService instance = new PieExecutorService();
        instance.setMap(map);
        instance.handlePieEvent(event);
    }
    
    /**
     * Test of handlePieEvent method, of class PieExecutorService.
     */
    @Test(expected = NullPointerException.class)
    public void testHandlePieEventNullValue() throws Exception {
        PieExecutorService instance = new PieExecutorService();
        instance.handlePieEvent(null);
    }

    /**
     * Test of registerTask method, of class PieExecutorService.
     */
    @Test
    public void testRegisterTask() {
        Map<Class, Class> map = Mockito.mock(Map.class);
        
        PieExecutorService instance = new PieExecutorService();
        instance.setMap(map);
        instance.registerTask(IPieEvent.class, IPieEventTask.class);
        
        Mockito.verify(map, Mockito.times(1)).put(IPieEvent.class, IPieEventTask.class);
    }
    
    /**
     * Test of registerTask method, of class PieExecutorService.
     */
    @Test(expected = NullPointerException.class)
    public void testRegisterTaskEventNullValue() {
        PieExecutorService instance = new PieExecutorService();
        instance.registerTask(null, IPieEventTask.class);
    }
    
    /**
     * Test of registerTask method, of class PieExecutorService.
     */
    @Test(expected = NullPointerException.class)
    public void testRegisterTaskTaskNullValue() {
        PieExecutorService instance = new PieExecutorService();
        instance.registerTask(IPieEvent.class, null);
    }
    
    @Test
    public void testRegisterExtendedTask() {
        Map<Class, Class> map = Mockito.mock(Map.class);
        
        PieExecutorService instance = new PieExecutorService();
        instance.setMap(map);
        
        class SubEvent implements IPieEvent {
        }
        
        class SubSubEvent extends SubEvent {
        }
        
        class SubTask implements IPieEventTask<SubEvent>{

            @Override
            public void setMsg(SubEvent msg) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void run() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        }
        
        instance.registerExtendedTask(SubSubEvent.class, SubTask.class);
        
        Mockito.verify(map, Mockito.times(1)).put(SubSubEvent.class, SubTask.class);
    }
}
