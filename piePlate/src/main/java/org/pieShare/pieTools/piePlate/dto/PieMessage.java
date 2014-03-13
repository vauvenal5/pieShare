package org.pieShare.pieTools.piePlate.dto;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class PieMessage {
    private byte[] message;

    public PieMessage(byte[] msg) {
        this.message = msg;
    }

    public byte[] getBuffer(){
        return this.message;
    }
}
