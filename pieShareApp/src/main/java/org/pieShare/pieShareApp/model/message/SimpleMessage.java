package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class SimpleMessage extends HeaderMessage implements IPrintableEvent {

	private String msg;

	public SimpleMessage() {
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public String getText() {
		return this.getMsg();
	}
}
