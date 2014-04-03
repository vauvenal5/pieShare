package org.pieShare.pieTools.pieUtilities.service.beanService;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class BeanService implements IBeanService, ApplicationContextAware {
    ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public <T> T getBean(Class<T> type) throws BeanServiceException {
        try {
            return context.getBean(type);
        } catch(Exception e) {
            throw new BeanServiceException(e);
        }
    }
}