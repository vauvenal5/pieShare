package org.pieshare.piespring.service.beanService;

public interface IBeanService {

	//todo: at some point also refactore everything above of pieSpring to new Provider pattern
	
	<T> T getBean(Class<T> type) throws BeanServiceError;

	//Object getBean(String name) throws BeanServiceError;
	public <T> T getBean(String beanID);
}
