/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileFilterService.filters;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieShareApp.service.userService.IUserService;


public class FileSizeFilter implements IFilter {

	private IUserService userService;
	private long maxFileSize = 0;
	
	public void setUserService(IUserService userService){
		this.userService = userService;
	}
	
	public void setMaxFileSize(long fSize){
		this.maxFileSize = fSize;
	}
	
	@Override
	public boolean matches(PieFile file) {
		maxFileSize = userService.getUser().getPieShareConfiguration().getMaxFileSize();
		//TODO sanity check
		long compSize = maxFileSize * 1000 * 1000;
		if(maxFileSize <= 0 || file.getSize() < compSize){
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean matches(PieFolder folder) {
		return true;
	}

}
