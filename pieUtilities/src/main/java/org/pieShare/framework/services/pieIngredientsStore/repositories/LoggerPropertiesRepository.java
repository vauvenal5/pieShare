package org.pieShare.framework.services.pieIngredientsStore.repositories;

import org.pieShare.framework.services.pieIngredientsStore.entities.LoggerPropertiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Richard on 09.01.14.
 */
@Repository
public interface LoggerPropertiesRepository extends JpaRepository<LoggerPropertiesEntity, Long> {
	List<LoggerPropertiesEntity> findByPropertyname(String propertyname);
}
