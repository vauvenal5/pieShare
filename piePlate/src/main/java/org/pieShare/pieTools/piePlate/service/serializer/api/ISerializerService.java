package org.pieShare.pieTools.piePlate.service.serializer.api;

import org.pieShare.pieTools.piePlate.model.message.api.IBasePieMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

/**
 * Created by Svetoslav on 19.01.14.
 */
public interface ISerializerService {

	IBasePieMessage deserialize(byte[] buffer) throws SerializerServiceException;

	byte[] serialize(IBasePieMessage msg) throws SerializerServiceException;
}
