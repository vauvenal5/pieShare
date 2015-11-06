/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.model.entities;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author Richard
 */
@Entity
public class FilterEntity extends BaseEntity {

	private String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
