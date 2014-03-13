package org.pieShare.pieTools.piePlate.service.integrationTests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pieShare.pieTools.piePlate.dto.PieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.exception.ClusterServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;


public class ClusterServiceTest {

    private ApplicationContext context = null;

    @Before
    public void before() {
        context = new ClassPathXmlApplicationContext("pieplate_test_context.xml");
    }

    @Test
    public void testClustering() throws Exception {
        IClusterService service1 = (IClusterService)context.getBean("clusterService");
        IClusterService service2 = (IClusterService)context.getBean("clusterService");

        service1.connect("myTestCluster");
        service2.connect("myTestCluster");

        Assert.assertEquals(2, service1.getMembersCount());
        Assert.assertEquals(2, service2.getMembersCount());
    }

    @Test
    public void testSendingMessage() throws Exception {

        ClusterServiceTestHelper tester1 = new ClusterServiceTestHelper((IClusterService)context.getBean("clusterService")) {
            @Override
            public void run() {
                try {
                    this.getService().connect("myTestCluster2");
                    this.getService().sendMessage(new PieMessage("Hello Pie!".getBytes()));
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
        new Thread(tester1).start();
        waitUntilDone(tester1);

        Assert.assertEquals(2, tester1.getService().getMembersCount());
        Assert.assertEquals(2, tester2.getService().getMembersCount());
    }

    private void waitUntilDone(ClusterServiceTestHelper testee) throws InterruptedException {
        while(!testee.getDone()){
            Thread.sleep(500);
        }
    }
}
