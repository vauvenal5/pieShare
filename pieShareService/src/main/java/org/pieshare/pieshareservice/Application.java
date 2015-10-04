/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author vauvenal5
 */
@SpringBootApplication
public class Application {
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//System.setProperty("java.net.preferIPv4Stack", "true");
		//todo: register contexts like in old main function
		//todo: at least until reconfiguration to autoconfiguration is done... will be done?
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		
//		String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }
	}
	
}
