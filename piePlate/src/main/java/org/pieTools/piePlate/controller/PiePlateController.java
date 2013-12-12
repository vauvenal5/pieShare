package org.pieTools.piePlate.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PiePlateController {

    private ApplicationContext context = null;

    public PiePlateController() {
        context = new ClassPathXmlApplicationContext("pieplate_application_context.xml");
    }



}



