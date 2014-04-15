package org.pieShare.pieTools.pieCeption.service.core.api;

import org.pieShare.pieTools.pieCeption.service.action.ICommand;
import org.pieShare.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface IConnectorService {
    boolean isPieShareRunning();
    void sendToMaster(ICommand command);
}
