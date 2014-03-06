package org.pieTools.piePlate.service.serializer;

import org.jgroups.util.Util;
import org.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class DefaultSerializerService implements ISerializerService {
    @Override
    public IPieMessage deserialize(byte[] buffer) throws SerializerServiceException {
        try {
            return (IPieMessage) Util.objectFromByteBuffer(buffer);
        } catch (Exception e) {
            throw new SerializerServiceException("Message couldn't be serialized!", e);
        }
    }

    @Override
    public byte[] serialize(IPieMessage msg) throws SerializerServiceException {
        try {
            return Util.objectToByteBuffer(msg);
        } catch (Exception e) {
            throw new SerializerServiceException("Message couldn't be serialized!", e);
        }
    }
}
