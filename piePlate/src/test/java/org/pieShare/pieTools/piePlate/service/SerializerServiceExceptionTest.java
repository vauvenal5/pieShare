/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service;

import java.io.IOException;
import junit.framework.Assert;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Svetoslav
 */
public class SerializerServiceExceptionTest {
	
	public SerializerServiceExceptionTest() {
	}

    //this test is just ment to test the code coverage set up
	@Test
	public void test() {
		SerializerServiceException ex = new SerializerServiceException("Test", new IOException());
		Assert.assertNotNull(ex);
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
	}
}
