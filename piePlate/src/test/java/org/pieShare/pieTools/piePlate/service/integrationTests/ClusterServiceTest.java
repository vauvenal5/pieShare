package org.pieShare.pieTools.piePlate.service.integrationTests;

import junit.framework.Assert;
import org.jgroups.JChannel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ObjectBasedReceiver;
import org.pieShare.pieTools.piePlate.service.helper.ClusterServiceTestHelper;
import org.pieShare.pieTools.piePlate.service.helper.TestMessage;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;


public class ClusterServiceTest {

    private JGroupsClusterService service1;
    private JGroupsClusterService service2;
    private IBeanService beanService;
    private IExecutorService executor1;
    private IExecutorService executor2;

    @Before
    public void before() throws Exception {                
        JacksonSerializerService serializer = new JacksonSerializerService();
        
        this.beanService = Mockito.mock(BeanService.class);
        this.executor1 = Mockito.mock(IExecutorService.class);
        this.executor2 = Mockito.mock(IExecutorService.class);
        
        ObjectBasedReceiver rec1 = new ObjectBasedReceiver();
        rec1.setBeanService(beanService);
        rec1.setSerializerService(serializer);
        rec1.setExecutorService(executor1);
        
        ObjectBasedReceiver rec2 = new ObjectBasedReceiver();
        rec2.setBeanService(beanService);
        rec2.setSerializerService(serializer);
        rec2.setExecutorService(executor2);
        
        JChannel channel1 = new JChannel();
        JChannel channel2 = new JChannel();
        
        this.service1 = new JGroupsClusterService();
        this.service1.setSerializerService(serializer);
        this.service1.setReceiver(rec1);
        this.service1.setChannel(channel1);
        
        this.service2 = new JGroupsClusterService();
        this.service2.setSerializerService(serializer);
        this.service2.setReceiver(rec2);
        this.service2.setChannel(channel2);
    }

    @Test
    public void testSimpleClustering() throws Exception {
        service1.connect("myTestCluster");
        service2.connect("myTestCluster");

        Assert.assertEquals(2, service1.getMembersCount());
        Assert.assertEquals(2, service2.getMembersCount());
    }

    @Test(timeout = 50000)
    public void testSendingMessage() throws Exception {
        
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
                    this.getService().sendMessage(msg);
                    this.setDone(true);
                } catch (ClusterServiceException e) {
                    e.printStackTrace();
                }
            }
        };

        final ClusterServiceTestHelper tester2 = new ClusterServiceTestHelper(this.service2) {
            @Override
            public void run() {
                try {
                    this.getService().connect("myTestCluster2");
                    this.setDone(true);
                } catch (ClusterServiceException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(tester2).start();
        waitUntilDone(tester2);
        tester2.setDone(false);
        
        Assert.assertEquals(1, tester2.getService().getMembersCount());
        
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TestMessage targetMsg = (TestMessage)invocation.getArguments()[0];
                Assert.assertEquals(msg.getMsg(), targetMsg.getMsg());
                tester2.setDone(true);
                return null;
            }
        }).when(this.executor2).handlePieEvent(Mockito.<TestMessage>any());
        
        new Thread(tester1).start();
        waitUntilDone(tester1);
        
        Assert.assertEquals(2, tester2.getService().getMembersCount());
        
        waitUntilDone(tester2);
    }

    private void waitUntilDone(ClusterServiceTestHelper testee) throws InterruptedException {
        while(!testee.getDone()){
            Thread.sleep(500);
        }
    }
}
