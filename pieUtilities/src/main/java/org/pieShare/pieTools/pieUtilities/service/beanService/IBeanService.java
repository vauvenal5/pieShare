package org.pieShare.pieTools.pieUtilities.service.beanService;

public interface IBeanService {

	<T> T getBean(Class<T> type) throws BeanServiceError;

	//Object getBean(String name) throws BeanServiceError;
	public <T> T getBean(String beanID);
}
