package org.pieTools.piePlate.dto;

import org.pieTools.piePlate.service.cluster.api.IPieMessage;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class PieMessage implements IPieMessage {
    private byte[] message;

    public PieMessage(byte[] msg) {
        this.message = msg;
    }

    public byte[] getBuffer(){
        return this.message;
    }
}
