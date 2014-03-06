package org.pieTools.pieUtilities.services.beanService;

public interface IBeanService {
    <T> T getBean(Class<T> type);
    Object getBean(String type);
}