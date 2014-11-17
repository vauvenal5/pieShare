/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.regexService;

/**
 *
 * @author Richard
 */
public interface IRegexService {

	void setPattern(String pattern);

	boolean matches(String text);

	String replaceAll(String text, String replaceString);
}
