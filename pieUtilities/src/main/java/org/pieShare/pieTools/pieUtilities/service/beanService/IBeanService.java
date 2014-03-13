package org.pieShare.pieTools.pieUtilities.service.beanService;

public interface IBeanService {
    <T> T getBean(Class<T> type);
    Object getBean(String type);
}