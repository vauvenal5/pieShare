/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareAppFx;

import javafx.util.Callback;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public class ControllerFactory implements Callback<Class<?>, Object> {

	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}
	
	@Override
	public Object call(Class<?> param) {
		return this.beanService.getBean(param);
	}
	
}
