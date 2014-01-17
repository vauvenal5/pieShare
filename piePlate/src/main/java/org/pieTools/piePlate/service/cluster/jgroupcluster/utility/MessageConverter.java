package org.pieTools.piePlate.service.cluster.jgroupcluster.utility;

import org.jgroups.Message;
import org.pieTools.piePlate.dto.PieMessage;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class MessageConverter {
    public static PieMessage convertMessageToPieMessage(Message msg) {
        PieMessage pMsg = new PieMessage(msg.getBuffer());
        return pMsg;
    }
}
