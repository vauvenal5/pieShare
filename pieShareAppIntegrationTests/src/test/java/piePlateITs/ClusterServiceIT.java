package piePlateITs;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import junit.framework.Assert;
import org.jgroups.JChannel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ObjectBasedReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import piePlateITs.helper.ClusterServiceTestHelper;
import piePlateITs.helper.TestMessage;

public class ClusterServiceIT {

	private JGroupsClusterService service1;
	private JGroupsClusterService service2;
	private JGroupsClusterService service3;
	private IBeanService beanService;
	private IExecutorService executor1;
	private IExecutorService executor2;
	private IExecutorService executor3;
	private JChannel channel1;
	private JChannel channel2;
	private JChannel channel3;
	private EncryptedPassword pwd;

	@Before
	public void before() throws Exception {
		JacksonSerializerService serializer = new JacksonSerializerService();

		this.beanService = Mockito.mock(IBeanService.class);
		this.executor1 = Mockito.mock(IExecutorService.class);
		this.executor2 = Mockito.mock(IExecutorService.class);
		this.executor3 = Mockito.mock(IExecutorService.class);

		ObjectBasedReceiver rec1 = new ObjectBasedReceiver();
		rec1.setBeanService(beanService);
		rec1.setSerializerService(serializer);
		rec1.setExecutorService(executor1);

		ObjectBasedReceiver rec2 = new ObjectBasedReceiver();
		rec2.setBeanService(beanService);
		rec2.setSerializerService(serializer);
		rec2.setExecutorService(executor2);

		ObjectBasedReceiver rec3 = new ObjectBasedReceiver();
		rec3.setBeanService(beanService);
		rec3.setSerializerService(serializer);
		rec3.setExecutorService(executor3);

		channel1 = new JChannel();
		channel2 = new JChannel();
		channel3 = new JChannel();

		this.service1 = new JGroupsClusterService();
		this.service1.setSerializerService(serializer);
		this.service1.setReceiver(rec1);
		this.service1.setChannel(channel1);

		this.service2 = new JGroupsClusterService();
		this.service2.setSerializerService(serializer);
		this.service2.setReceiver(rec2);
		this.service2.setChannel(channel2);

		this.service3 = new JGroupsClusterService();
		this.service3.setSerializerService(serializer);
		this.service3.setReceiver(rec3);
		this.service3.setChannel(channel3);
		
		pwd = new EncryptedPassword();
		PBEKeySpec keySpec = new PBEKeySpec("test".toCharArray());
		pwd.setSecretKey(SecretKeyFactory.getInstance("test").generateSecret(keySpec));
		pwd.setPassword("test".getBytes());
	}

	@Test(timeout = 50000)
	public void testSimpleClustering() throws Exception {
		String clusterName = "myTestCluster";

		ClusterServiceTestHelper tester1 = prepareSimpleConnectionHelper(service1, clusterName);
		ClusterServiceTestHelper tester2 = prepareSimpleConnectionHelper(service2, clusterName);
		ClusterServiceTestHelper tester3 = prepareSimpleConnectionHelper(service3, clusterName);

		startTester(tester1, false);
		startTester(tester2, false);
		startTester(tester3, false);

		waitUntilDone(tester1);
		waitUntilDone(tester2);
		waitUntilDone(tester3);

		Assert.assertEquals(3, service1.getMembersCount());
		Assert.assertEquals(3, service2.getMembersCount());
		Assert.assertEquals(3, service3.getMembersCount());
	}

	@Test(timeout = 50000)
	public void testSendingMessageToMany() throws Exception {

		final TestMessage msg = new TestMessage();
		msg.setType(TestMessage.class.getName());
		msg.setMsg("This is a msg!");
		msg.setAddress(new JGroupsPieAddress());

		Mockito.when(this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress())).thenReturn(new JGroupsPieAddress());

		ClusterServiceTestHelper tester1 = new ClusterServiceTestHelper(this.service1) {
			@Override
			public void run() {
				try {
					this.getService().connect("myTestCluster2");
					this.getService().sendMessage(msg, pwd);
					this.setDone(true);
				} catch (ClusterServiceException e) {
					e.printStackTrace();
				}
			}
		};

		final ClusterServiceTestHelper tester2 = prepareSimpleConnectionHelper(this.service2, "myTestCluster2");
		final ClusterServiceTestHelper tester3 = prepareSimpleConnectionHelper(this.service3, "myTestCluster2");

		prepareMessageCheckOnMock(tester2, executor2, msg);
		prepareMessageCheckOnMock(tester3, executor3, msg);

		startTester(tester2, true);
		Assert.assertEquals(1, tester2.getService().getMembersCount());

		startTester(tester3, true);
		Assert.assertEquals(2, tester3.getService().getMembersCount());

		new Thread(tester1).start();
		waitUntilDone(tester1);

		Assert.assertEquals(3, tester2.getService().getMembersCount());

		waitUntilDone(tester2);
		waitUntilDone(tester3);
	}

	@Test(timeout = 50000)
	public void testSendingMessageToSingle() throws Exception {

		final String clusterName = "myTestCluster3";

		final TestMessage msg = new TestMessage();
		msg.setType(TestMessage.class.getName());
		msg.setMsg("This is a msg!");
		msg.setAddress(new JGroupsPieAddress());

		Mockito.when(this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress())).thenReturn(new JGroupsPieAddress());

		ClusterServiceTestHelper tester1 = new ClusterServiceTestHelper(this.service1) {
			@Override
			public void run() {
				try {
					this.getService().connect(clusterName);
					this.getService().sendMessage(msg, pwd);
					this.setDone(true);
				} catch (ClusterServiceException e) {
					e.printStackTrace();
				}
			}
		};

		final ClusterServiceTestHelper tester2 = prepareSimpleConnectionHelper(this.service2, clusterName);
		final ClusterServiceTestHelper tester3 = prepareSimpleConnectionHelper(this.service3, clusterName);

		prepareMessageCheckOnMock(tester2, executor2, msg);

		startTester(tester2, true);
		startTester(tester3, false);

		JGroupsPieAddress ad = new JGroupsPieAddress();
		ad.setAddress(this.channel2.getAddress());
		msg.setAddress(ad);

		startTester(tester1, false);

		waitUntilDone(tester2);
		Mockito.verify(this.executor3, Mockito.times(0)).handlePieEvent(Mockito.any(TestMessage.class));
	}

	@Test(timeout = 50000)
	public void testSendingManyMessagesToSingle() throws Exception {
		final String clusterName = "myTestCluster4";

		final TestMessage msg = new TestMessage();
		msg.setType(TestMessage.class.getName());

		final String value = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		Integer expectedCountPerLetter = 2;
		final char[] values = value.toCharArray();

		Mockito.when(this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress())).thenReturn(new JGroupsPieAddress());

		ClusterServiceTestHelper tester1 = new ClusterServiceTestHelper(this.service1) {
			@Override
			public void run() {
				try {
					this.getService().connect(clusterName);

					for (int i = 0; i < values.length; i++) {
						msg.setMsg(String.valueOf(values[i]));
						this.getService().sendMessage(msg, pwd);
					}

					this.setDone(true);
				} catch (ClusterServiceException e) {
					e.printStackTrace();
				}
			}
		};

		final ClusterServiceTestHelper tester2 = prepareSimpleConnectionHelper(service2, clusterName);

		startTester(tester2, true);

		final Map<String, Integer> rec = new HashMap<String, Integer>();

		Mockito.doAnswer(new Answer() {
			private int numRec = 0;

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				TestMessage targetMsg = (TestMessage) invocation.getArguments()[0];
				//System.out.println(targetMsg.getMsg());
				Integer num = 1;
				numRec++;
				if (rec.containsKey(targetMsg.getMsg())) {
					num = rec.get(targetMsg.getMsg()) + 1;
				}

				rec.put(targetMsg.getMsg(), num);

				if (numRec == values.length) {
					tester2.setDone(true);
				}

				return null;
			}
		}).when(this.executor2).handlePieEvent(Mockito.<TestMessage>any());

		JGroupsPieAddress ad = new JGroupsPieAddress();
		ad.setAddress(this.channel2.getAddress());
		msg.setAddress(ad);

		startTester(tester1, false);

		waitUntilDone(tester2);

		for (int i = 0; i < values.length; i++) {
			String key = String.valueOf(values[i]);
			Assert.assertTrue("Failed on letter: " + values[i], rec.containsKey(key));
			Assert.assertEquals(expectedCountPerLetter, rec.get(key));
		}
	}

	private void startTester(ClusterServiceTestHelper tester, boolean resetDone) throws Exception {
		new Thread(tester).start();
		waitUntilDone(tester);
		if (resetDone) {
			tester.setDone(false);
		}
	}

	private ClusterServiceTestHelper prepareSimpleConnectionHelper(IClusterService service, final String clusterName) {
		ClusterServiceTestHelper tester = new ClusterServiceTestHelper(service) {
			@Override
			public void run() {
				try {
					this.getService().connect(clusterName);
					this.setDone(true);
				} catch (ClusterServiceException e) {
					e.printStackTrace();
				}
			}
		};

		return tester;
	}

	private void prepareMessageCheckOnMock(final ClusterServiceTestHelper tester, IExecutorService mock, final TestMessage expected) throws Exception {
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				TestMessage targetMsg = (TestMessage) invocation.getArguments()[0];
				Assert.assertEquals(expected.getMsg(), targetMsg.getMsg());
				tester.setDone(true);
				return null;
			}
		}).when(mock).handlePieEvent(Mockito.<TestMessage>any());
	}

	private void waitUntilDone(ClusterServiceTestHelper testee) throws InterruptedException {
		while (!testee.getDone()) {
			Thread.sleep(500);
		}
	}
}
