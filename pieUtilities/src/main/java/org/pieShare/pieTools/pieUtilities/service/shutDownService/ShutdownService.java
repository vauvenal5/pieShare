/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.shutDownService;

import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav
 */
public class ShutdownService implements IShutdownService {
	
	private final List<IShutdownableService> listeners = new ArrayList<>();

	public ShutdownService(){}
	
	public void setListener(IShutdownableService service) {
		this.registerListener(service);
	}

	@Override
	public void registerListener(IShutdownableService service) {
		this.listeners.add(service);
	}

	@Override
	public void fireShutdown() {
		for(IShutdownableService service : this.listeners) {
			service.shutdown();
		}
	}
}
