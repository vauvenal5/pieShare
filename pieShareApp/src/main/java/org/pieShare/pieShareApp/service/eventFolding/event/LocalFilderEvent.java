/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.eventFolding.event;

import java.util.EventObject;
import org.pieShare.pieShareApp.model.LocalFileEvent;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class LocalFilderEvent extends EventObject {
	
	private LocalFileEvent localFileEvent;
	
	public LocalFilderEvent(Object source, LocalFileEvent event) {
		super(source);
		this.localFileEvent = event;
	}

	public LocalFileEvent getLocalFileEvent() {
		return localFileEvent;
	}
}
