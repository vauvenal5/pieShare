package org.pieShare.pieShareApp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class App {
    public static void main(String[] args) {
        //todo-sv: choosing the right ip stack should be automated and not fixed to IPv4
        System.setProperty("java.net.preferIPv4Stack", "true");
        ApplicationContext context = new ClassPathXmlApplicationContext("pieShareApplicationContext.xml");
        //PieShareController controller = (PieShareController)context.getBean("pieShareController");
        //controller.run();
    }
}
