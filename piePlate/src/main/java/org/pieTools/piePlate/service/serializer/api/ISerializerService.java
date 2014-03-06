package org.pieTools.piePlate.service.serializer.api;

import org.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

/**
 * Created by Svetoslav on 19.01.14.
 */
public interface ISerializerService {
    IPieMessage deserialize(byte[] buffer) throws SerializerServiceException;

    byte[] serialize(IPieMessage msg) throws SerializerServiceException;
}
