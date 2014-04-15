package org.pieShare.pieTools.pieUtilities.service.beanService;

import org.springframework.context.ApplicationContext;

public interface IBeanService {
    <T> T getBean(Class<T> type) throws BeanServiceException;
    Object getBean(String name) throws BeanServiceException;
}