package org.pieShare.pieTools.pieCeption.service.core.api;

import org.pieShare.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface IPieCeptionService {
    void start() throws PieCeptionServiceException;
    void handlePieMessage(IPieMessage message);
}
