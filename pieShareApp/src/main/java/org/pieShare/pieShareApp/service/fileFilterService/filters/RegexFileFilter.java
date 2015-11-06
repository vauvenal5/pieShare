/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService.filters;

import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.service.regexService.IRegexService;

/**
 *
 * @author Richard
 */
public class RegexFileFilter implements IFilter {

	private IRegexService regexService;
	private String patternText;

	public void setRegexService(IRegexService regexService) {
		this.regexService = regexService;
	}

	@Override
	public void setPattern(String pattern) {
		this.patternText = pattern;
		regexService.setPattern(patternText);
	}

	@Override
	public String getPattern() {
		return patternText;
	}

	@Override
	public boolean matches(String text) {
		return regexService.matches(text);
	}
}
