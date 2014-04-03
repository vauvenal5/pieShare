/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api;

import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;

/**
 *
 * @author Svetoslav
 */
public interface IExecutorService {
    
    public void execute(IPieTask task);
    
    <P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<P> event, Class<T> task);
    
    <X extends P, P extends IPieEvent, T extends IPieEventTask<P>> void registerExtendedTask(Class<X> event, Class<T> task);
    
    public void handlePieEvent(IPieEvent event) throws PieExecutorServiceException;
}
