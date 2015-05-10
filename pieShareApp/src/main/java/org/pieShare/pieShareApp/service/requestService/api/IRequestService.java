/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.requestService.api;

import org.pieShare.pieShareApp.model.message.api.IMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Richard
 */
public interface IRequestService {

	public void requestFile(PieFile pieFile);

	public boolean isRequested(PieFile file);
	
	public boolean handleRequest(PieFile file);

	public boolean deleteRequestedFile(PieFile pieFile);

	//public void checkForActiveFileHandle(PieFile pieFile);
}
