/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.shutDownService.api;

/**
 *
 * @author Svetoslav
 */
public interface IShutdownableService {
	
	default void setShutdownService(IShutdownService service) {
		service.registerListener(this);
	}
	
	void shutdown();
	
}
