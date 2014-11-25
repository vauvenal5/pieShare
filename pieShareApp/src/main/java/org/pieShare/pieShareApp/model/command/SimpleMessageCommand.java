/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.command;

import org.pieShare.pieTools.pieUtilities.model.command.ICommand;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;

/**
 *
 * @author Svetoslav
 */
public class SimpleMessageCommand extends BaseCommand<IPieCallable> implements ICommand<IPieCallable> {

	private String msg;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
