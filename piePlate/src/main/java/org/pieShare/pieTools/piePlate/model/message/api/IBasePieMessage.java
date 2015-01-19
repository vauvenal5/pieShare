/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.api;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;

/**
 *
 * @author Richard
 */
public interface IBasePieMessage extends IPieEvent{
	
	String getType();

	void setType(String type);
}
