package org.pieTools.piePlate.service.integrationTests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pieTools.piePlate.service.cluster.api.IClusterService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


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

}
