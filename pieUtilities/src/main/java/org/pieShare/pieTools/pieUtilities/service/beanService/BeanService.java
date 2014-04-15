package org.pieShare.pieTools.pieUtilities.service.beanService;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class BeanService implements IBeanService, ApplicationContextAware {
    ApplicationContext context;

    //is autoset
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * DEPRECATED! DO NOT USE! WILL BE REMOVED!
     * @param <T>
     * @param type
     * @return
     * @throws BeanServiceException 
     */
    @Override
    public <T> T getBean(Class<T> type) throws BeanServiceException {
        try {
            return context.getBean(type);
        } catch(Exception e) {
            throw new BeanServiceException(e);
        }
    }

    @Override
    public Object getBean(String name) throws BeanServiceException {
        try {
            return context.getBean(name);
        } catch(Exception e) {
            throw new BeanServiceException(e);
        }
    }
}