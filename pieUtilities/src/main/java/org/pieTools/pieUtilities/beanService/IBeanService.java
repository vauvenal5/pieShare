package org.pieTools.pieUtilities.beanService;

public interface IBeanService {
    <T> T getBean(Class<T> type);
}