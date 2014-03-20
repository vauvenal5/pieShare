package org.pieShare.pieTools.piePlate.model.message;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 19.03.14.
 */
public class HeaderMessage implements IPieMessage {
    private String type;

    public HeaderMessage() {
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
