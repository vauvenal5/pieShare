/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.model.command;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieCallbackEvent;

/**
 *
 * @author Svetoslav
 */
public interface ICommand<C extends IPieCallable> extends IPieCallbackEvent<C>{

}
