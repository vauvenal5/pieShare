/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.loopHoleMessages;

import org.pieShare.pieTools.piePlate.model.message.UdpMessage;

/**
 *
 * @author Richard
 */
public class LoopHoleAckMessage extends UdpMessage{

	private String senderID;

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String from) {
		this.senderID = from;
	}
}
