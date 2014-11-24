package org.pieShare.pieTools.piePlate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.piePlate.service.unitTests.helper.TestMessage;

/**
 * Created by Svetoslav on 19.03.14.
 */
public class JacksonSerializerTest {

	private byte[] testBuffer;
	private TestMessage msg;
	private JacksonSerializerService service;

	@Before()
	public void before() throws Exception {
		msg = new TestMessage();
		msg.setType(TestMessage.class.getName());
		msg.setMsg("Testing JacksonSerializer!");

		ObjectMapper mapper = new ObjectMapper();
		testBuffer = mapper.writeValueAsBytes(msg);

		this.service = new JacksonSerializerService();
	}

	@Test
	public void testSerialize() throws Exception {
		byte[] res = this.service.serialize(this.msg);
		Assert.assertArrayEquals(this.testBuffer, res);
	}

	@Test
	public void testDeserialize() throws Exception {
		IPieMessage res = this.service.deserialize(this.testBuffer);

		Assert.assertEquals(TestMessage.class, res.getClass());
		Assert.assertEquals(this.msg.getMsg(), ((TestMessage) res).getMsg());
		Assert.assertEquals(this.msg.getType(), ((TestMessage) res).getType());
	}
}
