/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.model;

import javax.json.JsonObject;
import org.pieShare.pieShareServer.services.api.IIncomeTask;

/**
 *
 * @author Richard
 */
public class UserData {

	private JsonObject connectionInfo;
	private IIncomeTask task;

	public JsonObject getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(JsonObject connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	public IIncomeTask getTask() {
		return task;
	}

	public void setTask(IIncomeTask task) {
		this.task = task;
	}
}
