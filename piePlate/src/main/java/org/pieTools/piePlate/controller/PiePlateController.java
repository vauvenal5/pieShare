package org.pieTools.piePlate.controller;

import org.pieTools.piePlate.controller.api.ICluster;
import org.pieTools.piePlate.controller.exception.ClusterException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PiePlateController implements ICluster {

    private ApplicationContext context = null;

    public PiePlateController() {
        context = new ClassPathXmlApplicationContext("pieplate_application_context.xml");
    }


    @Override
    public void joinCluster(String cloudName) throws ClusterException {

    }

    @Override
    public void leafCluster(String cloudName) throws ClusterException {

    }
}



