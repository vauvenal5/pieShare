/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api;

import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public interface IExecutorService {
    
    public void execute(IPieTask task);
    
    <P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<P> msg, Class<T> task);
    
    public void handlePieEvent(IPieEvent event);
}
