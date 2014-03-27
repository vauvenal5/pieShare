/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorService implements IExecutorService{
    
    private ExecutorService executor;
    private Map<Class, Class> tasks;
    private IBeanService beanService;

    @Override
    public void execute(IPieTask task) {
        Validate.notNull(task);
        this.executor.execute(task);
    }
    
    @Override
    public void handlePieEvent(IPieEvent event) {
        Class taskClass = this.tasks.get(event.getClass());
        IPieEventTask task = (IPieEventTask)this.beanService.getBean(taskClass);
        Validate.notNull(task);
        task.setMsg(event);
        this.executor.execute(task);
    }

    @Override
    public <P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<P> msg, Class<T> task) {
        this.tasks.put(msg, task);
    }
    
}
