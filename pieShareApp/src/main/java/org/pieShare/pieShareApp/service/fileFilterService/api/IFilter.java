/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService.api;

import org.pieShare.pieShareApp.model.entities.FilterEntity;

/**
 *
 * @author Richard
 */
public interface IFilter {

	void setPattern(String pattern);

	String getPattern();

	boolean matches(String text);

	FilterEntity getEntity();

	void setEntity(FilterEntity entity);
}
