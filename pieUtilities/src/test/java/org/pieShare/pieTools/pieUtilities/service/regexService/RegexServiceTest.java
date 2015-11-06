/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.regexService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Richard
 */
public class RegexServiceTest {

	public RegexServiceTest() {
	}

	/**
	 * Test of matches method, of class RegexService.
	 */
	@Test
	public void testMatches() {
		Pattern p = Pattern.compile(".*thumbs\\.db.*");
		Matcher m = p.matcher("/test/test/test/thumbs.db/");
		boolean b = m.matches();
		Assert.assertTrue(b);
	}

}
