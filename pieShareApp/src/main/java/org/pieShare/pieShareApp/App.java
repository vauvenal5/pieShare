package org.pieShare.pieShareApp;

import org.pieShare.pieShareApp.controller.PieShareController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class App {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("pieShareApplicationContext.xml");
        PieShareController controller = (PieShareController)context.getBean("pieShareController");
        controller.run();
    }
}
