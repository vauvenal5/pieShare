/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.model.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import org.pieShare.pieShareApp.model.entities.api.IFileFilterEntity;

/**
 *
 * @author Richard
 */
@Entity
public class FilterEntity extends BaseEntity implements IFileFilterEntity {

	private String pattern;

        @Override
	public String getPattern() {
		return pattern;
	}

        @Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
