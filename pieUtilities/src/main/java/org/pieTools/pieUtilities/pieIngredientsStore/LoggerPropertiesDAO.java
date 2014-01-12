package org.pieTools.pieUtilities.pieIngredientsStore;

import org.hibernate.Hibernate;
import org.pieTools.pieUtilities.pieIngredientsStore.entities.LoggerPropertiesEntity;
import org.pieTools.pieUtilities.pieIngredientsStore.repositories.LoggerPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Richard on 09.01.14.
 */
@Component
public class LoggerPropertiesDAO {

	@Autowired
	private LoggerPropertiesRepository loggerPropertiesRepository;

	public LoggerPropertiesDAO() {
	}

	public void setNewProperty(Properties property, String name) {

		final LoggerPropertiesEntity entity = new LoggerPropertiesEntity();
		entity.setPropertyname(name);

		Map<String, String> map = new HashMap<String, String>();

		for (Object k : property.keySet()) {
			String key = (String) k;
			String value = property.getProperty(key);
			map.put(key, value);
		}

		entity.setProperties(map);
		loggerPropertiesRepository.save(entity);
	}

	public Properties getPropertyByName(String name) {

		final List<LoggerPropertiesEntity> entities = loggerPropertiesRepository.findByPropertyname(name);
		if (entities == null || entities.size() <= 0) {
			return null;
		}
		//Hibernate.initialize(entities);
		//entities.size();
		Map<String, String> map = entities.get(0).getProperties();

		Properties loggerPro = new Properties();

		for (String key : map.keySet()) {
			String value = map.get(key);
			loggerPro.setProperty(key, value);
		}
		return loggerPro;
	}
}
