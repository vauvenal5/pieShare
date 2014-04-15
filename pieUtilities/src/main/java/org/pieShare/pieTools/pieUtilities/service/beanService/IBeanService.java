package org.pieShare.pieTools.pieUtilities.service.beanService;

import org.springframework.context.ApplicationContext;

public interface IBeanService {
    <T> T getBean(Class<T> type) throws BeanServiceException;
 
    public <T> T getBean(Class<T> type, String beanID) throws BeanServiceException;
}