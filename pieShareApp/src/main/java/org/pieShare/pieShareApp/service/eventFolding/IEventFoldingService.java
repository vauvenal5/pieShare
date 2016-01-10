/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.eventFolding;

import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.service.eventFolding.event.ILocalFilderEventListener;
import org.pieShare.pieShareApp.service.eventFolding.event.LocalFilderEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface IEventFoldingService {
	public void handleLocalEvent(LocalFileEvent event);
	public IEventBase<ILocalFilderEventListener, LocalFilderEvent> getLocalFilderEventBase();
}
