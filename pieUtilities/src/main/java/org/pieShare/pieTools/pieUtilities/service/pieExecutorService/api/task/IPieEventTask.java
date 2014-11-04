/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;

/**
 *
 * @author Svetoslav
 */
public interface IPieEventTask<P extends IPieEvent> extends IPieTask {
	void setEvent(P msg);
}
