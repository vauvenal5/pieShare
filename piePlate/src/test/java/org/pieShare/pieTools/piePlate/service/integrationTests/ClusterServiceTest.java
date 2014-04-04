package org.pieShare.pieTools.piePlate.service.integrationTests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.helper.ClusterServiceTestHelper;
import org.pieShare.pieTools.piePlate.service.helper.TestMessage;
import org.pieShare.pieTools.piePlate.service.helper.TestServiceCallback;
import org.pieShare.pieTools.piePlate.service.helper.TestTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ClusterServiceTest {

    private ApplicationContext context = null;

    @Before
    public void before() {
        context = new ClassPathXmlApplicationContext("pieplate_test_context.xml");
    }

    @Test
    public void testSimpleClustering() throws Exception {
        IClusterService service1 = (IClusterService)context.getBean("clusterService");
        IClusterService service2 = (IClusterService)context.getBean("clusterService");

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
        
        PieExecutorService executor = (PieExecutorService)context.getBean("pieExecutorService");
        executor.registerTask(TestMessage.class, TestTask.class);

        ClusterServiceTestHelper tester1 = new ClusterServiceTestHelper((IClusterService)context.getBean("clusterService")) {
            @Override
            public void run() {
                try {
                    this.getService().connect("myTestCluster2");
                    this.getService().sendMessage(msg);
                    this.setDone();
                } catch (ClusterServiceException e) {
                    e.printStackTrace();
                }
            }
        };

        ClusterServiceTestHelper tester2 = new ClusterServiceTestHelper((IClusterService)context.getBean("clusterService")) {
            @Override
            public void run() {
                try {
                    this.getService().connect("myTestCluster2");
                    this.setDone();
                } catch (ClusterServiceException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(tester2).start();
        waitUntilDone(tester2);
        Assert.assertEquals(1, tester2.getService().getMembersCount());
        new Thread(tester1).start();
        waitUntilDone(tester1);
        
        TestTask task = ((TestServiceCallback)context.getBean("testServiceCallback")).getTask();
        
        while(task == null){
            Thread.sleep(500);
            task = ((TestServiceCallback)context.getBean("testServiceCallback")).getTask();
        }

        Assert.assertEquals(2, tester1.getService().getMembersCount());
        Assert.assertEquals(2, tester2.getService().getMembersCount());
        Assert.assertEquals(msg.getMsg(), task.getMsg().getMsg());
        Assert.assertEquals(true, task.getRun());
    }

    private void waitUntilDone(ClusterServiceTestHelper testee) throws InterruptedException {
        while(!testee.getDone()){
            Thread.sleep(500);
        }
    }
}
