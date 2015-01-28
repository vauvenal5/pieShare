package org.pieShare.pieTools.piePlate.model.message.api;

import java.io.Serializable;
import org.pieShare.pieTools.piePlate.model.IPieAddress;


/**
 * Created by Svetoslav on 17.01.14.
 */
public interface IClusterMessage extends IPieMessage {

	IPieAddress getAddress();

	void setAddress(IPieAddress address);
}
