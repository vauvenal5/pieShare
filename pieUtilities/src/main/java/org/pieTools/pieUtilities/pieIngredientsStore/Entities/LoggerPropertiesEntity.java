package org.pieTools.pieUtilities.pieIngredientsStore.entities;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by Richard on 09.01.14.
 */
@Entity
@Table(name = "loggerProperties")
public class LoggerPropertiesEntity {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "propertyname")
	private String propertyname;

	@ManyToMany
	@Column(name="Properties")
	private Map<String,String> properties;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Map<String, String> getProperties(){
		return properties;
	}


	public void setProperties(Map<String, String> properties){
		this.properties = properties;
	}

	public void setPropertyname(String propertyname){
		this.propertyname = propertyname;
	}

	public String getPropertyname(){
		return this.propertyname;
	}
}
