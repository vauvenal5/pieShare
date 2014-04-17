package org.pieShare.pieTools.pieCeption.service.api;

import org.pieShare.pieTools.pieCeption.model.action.ICommandMessage;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface IConnectorService {
    boolean isPieShareRunning();
    void sendToMaster(ICommandMessage command);
}
