/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.springConfiguraton;

import org.pieShare.pieShareClient.services.ClientSendTask;
import org.pieShare.pieShareClient.services.ClientTask;
import org.springframework.context.annotation.Configuration;


/**
 *
 * @author Richard
 */
@Configuration
public class PieShareClientServiceConfiguration {

	public ClientSendTask clientSendTask()
	{
		ClientSendTask task = new ClientSendTask();
		return task;
	}

}
