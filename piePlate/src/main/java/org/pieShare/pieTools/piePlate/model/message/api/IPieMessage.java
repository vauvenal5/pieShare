package org.pieShare.pieTools.piePlate.model.message.api;

import java.io.Serializable;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;

/**
 * Created by Svetoslav on 17.01.14.
 */
public interface IPieMessage extends IBasePieMessage {

	IPieAddress getAddress();

	void setAddress(IPieAddress address);

	String getType();

	void setType(String type);

}
