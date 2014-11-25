package org.pieShare.pieTools.piePlate.model.message;

import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 19.03.14.
 */
public class HeaderMessage implements IPieMessage {

	private String type;
	private IPieAddress address;

	public HeaderMessage() {
		this.setType(this.getClass().getName());
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public IPieAddress getAddress() {
		return this.address;
	}

	@Override
	public void setAddress(IPieAddress address) {
		this.address = address;
	}
}
