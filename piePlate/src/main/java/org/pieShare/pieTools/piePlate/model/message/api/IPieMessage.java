package org.pieShare.pieTools.piePlate.model.message.api;

import java.io.Serializable;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;

/**
 * Created by Svetoslav on 17.01.14.
 */
public interface IPieMessage extends IPieEvent {
    public IPieAddress getAddress();
    public void setAddress(IPieAddress address);
    
    public String getType();
    public void setType(String type);
}
