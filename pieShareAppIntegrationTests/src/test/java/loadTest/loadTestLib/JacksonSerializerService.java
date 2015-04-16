package loadTest.loadTestLib;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import loadTest.loadTestLib.api.ISerializerService;

public class JacksonSerializerService implements ISerializerService {

    private ObjectMapper objectMapper;

    public JacksonSerializerService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public ArrayList<Integer> deserialize(byte[] buffer) throws IOException {
        ArrayList<Integer> args;

        args = objectMapper.readValue(buffer, ArrayList.class);

        return args;
    }
}
