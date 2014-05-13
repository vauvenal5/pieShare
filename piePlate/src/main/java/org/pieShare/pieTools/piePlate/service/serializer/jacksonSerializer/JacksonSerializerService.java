package org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

import java.io.IOException;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.IPieMessageMixIn;

/**
 * Created by Svetoslav on 19.03.14.
 */
public class JacksonSerializerService implements ISerializerService {

    private ObjectMapper objectMapper;

    public JacksonSerializerService() {   
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.addMixInAnnotations(IPieMessage.class, IPieMessageMixIn.class);
    }

    @Override
    public IPieMessage deserialize(byte[] buffer) throws SerializerServiceException {
        HeaderMessage header;

        try {
            header = objectMapper.readValue(buffer, HeaderMessage.class);
        } catch (IOException e) {
            throw new SerializerServiceException("Could not deserialize header!", e);
        }

        IPieMessage msg;

        try {
            msg = (IPieMessage)objectMapper.readValue(buffer, Class.forName(header.getType()));
        } catch (IOException e) {
            throw new SerializerServiceException("Could not deserialize msg!", e);
        } catch (ClassNotFoundException e) {
            throw new SerializerServiceException("Could not find given class name!", e);
        }

        return msg;
    }

    @Override
    public byte[] serialize(IPieMessage msg) throws SerializerServiceException {
        try {
            return objectMapper.writeValueAsBytes(msg);
        } catch (JsonProcessingException e) {
            throw new SerializerServiceException("Failed serializing JSON", e);
        }
    }
}
