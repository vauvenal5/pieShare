/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.idService;

import java.util.UUID;
import org.pieShare.pieTools.pieUtilities.service.idService.api.IIDService;

/**
 *
 * @author Richard
 */
public class SimpleUUIDService implements IIDService{

	@Override
	public String getNewID() {
		return UUID.randomUUID().toString();
	}
	
}
