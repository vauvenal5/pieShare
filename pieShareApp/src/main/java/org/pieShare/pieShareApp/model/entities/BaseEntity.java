/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;

/**
 *
 * @author Richard
 */
@MappedSuperclass
public class BaseEntity implements IBaseEntity {

	@Id
	@GeneratedValue
	private long id;

	public long getId() {
		return id;
	}
}
