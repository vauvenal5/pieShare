package org.pieShare.framework.services.beanService;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class BeanService implements IBeanService, ApplicationContextAware {
    ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public Object getBean(String type) {
        return context.getBean(type);
    }

}