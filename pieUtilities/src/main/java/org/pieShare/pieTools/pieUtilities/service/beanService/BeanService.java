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
	 *
	 * @param <T>
	 * @param type
	 * @return
	 * @throws BeanServiceError
	 */
	@Override
	public <T> T getBean(Class<T> type) throws BeanServiceError {
		try {
			return context.getBean(type);
		} catch (Exception e) {
			throw new BeanServiceError(e);
		}
	}

	/*@Override
	 public Object getBean(String name) throws BeanServiceError {
	 try {
	 return context.getBean(name);
	 } catch(Exception e) {
	 throw new BeanServiceError(e);
	 }
	 }*/
	@Override
	public <T> T getBean(String beanID) {
		try {

			Object o = context.getBean(beanID);
			return (T) o;
		} catch (Exception e) {
			throw new BeanServiceError(e);
		}
	}
}
