/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;

/**
 *
 * @author Svetoslav
 */
public interface IPieExecutorTaskFactory {
	IPieEventTask getTask(IPieEvent event) throws PieExecutorTaskFactoryException;
	
	/**
	 * Will register a task to a event it also works with the base type of a 
	 * given event type. Lets say for example you want to print SmallMessages and
	 * ErrorMessages to CMD and BigMessages to file. All implement the
	 * PrintableEvent. You have now two tasks: PrintToCMDTask<PrintableEvent>
	 * and PrintToFileTask<PrintableEvent>
	 * Both can handle all Messages which are Printable. You have to register
	 * the tasks based on the derived types because registering to the base type
	 * will always trigger the tasks although the tasks work with the base type
	 * and not the given derived type.
	 *
	 * @param <X>
	 * @param <P>
	 * @param <T>
	 * @param event
	 * @param task
	 */
	<X extends P, P extends IPieEvent, T extends IPieEventTask<P>> void registerTask(Class<X> event, Class<T> task);
	
	<P extends IPieEvent> void removeTaskRegistration(Class<P> event);
}
