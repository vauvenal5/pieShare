package org.pieShare.pieTools.piePlate.service.unitTests.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class TestMessage implements IPieMessage {

	String msg;
	String type;
	IPieAddress address;

	public TestMessage() {
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	@JsonIgnore
	public IPieAddress getAddress() {
		return this.address;
	}

	@Override
	public void setAddress(IPieAddress address) {
		this.address = address;
	}
}
