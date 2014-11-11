/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.regexService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Richard
 */
public class RegexService implements IRegexService {

	private Pattern pattern;
	private Matcher matcher;

	@Override
	public void setPattern(String textPattern) {
		pattern = Pattern.compile(textPattern);
	}

	@Override
	public boolean matches(String text) {
		matcher = pattern.matcher(text);
		return matcher.matches();
	}

	@Override
	public String replaceAll(String text, String replaceString) {
		matcher = pattern.matcher(text);
		if (matcher.matches()) {
			return matcher.replaceAll(replaceString);
		}
		return text;
	}
}
