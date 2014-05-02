/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.base64Service;

import java.io.UnsupportedEncodingException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;

/**
 *
 * @author richy
 */
public class Base64ServiceTest
{

    public Base64ServiceTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of encode method, of class Base64Service.
     */
    @Test
    public void testEncode() throws UnsupportedEncodingException
    {
        String data = "Zu Tyonis dem Türannen, schlich Damon, den Dolche im gewande. Ihn schlugen die Häscher in Bande. Was wolltest du mit dem Dolche, Sprich!";
        String base64Data = "WnUgVHlvbmlzIGRlbSBUw7xyYW5uZW4sIHNjaGxpY2ggRGFtb24sIGRlbiBEb2xjaGUgaW0gZ2V3YW5kZS4gSWhuIHNjaGx1Z2VuIGRpZSBIw6RzY2hlciBpbiBCYW5kZS4gV2FzIHdvbGx0ZXN0IGR1IG1pdCBkZW0gRG9sY2hlLCBTcHJpY2gh";

        IBase64Service service = new Base64Service();

        byte[] erg = service.encode(data.getBytes("UTF-8"));
        String stg = new String(erg);

        Assert.assertEquals(base64Data, stg);
    }

    @Test
    public void testDecode() throws UnsupportedEncodingException
    {
        String data = "Zu Tyonis dem Türannen, schlich Damon, den Dolche im gewande. Ihn schlugen die Häscher in Bande. Was wolltest du mit dem Dolche, Sprich!";

        String base64Data = "WnUgVHlvbmlzIGRlbSBUw7xyYW5uZW4sIHNjaGxpY2ggRGFtb24sIGRlbiBEb2xjaGUgaW0gZ2V3YW5kZS4gSWhuIHNjaGx1Z2VuIGRpZSBIw6RzY2hlciBpbiBCYW5kZS4gV2FzIHdvbGx0ZXN0IGR1IG1pdCBkZW0gRG9sY2hlLCBTcHJpY2gh";

        IBase64Service service = new Base64Service();

        byte[] erg = service.decode(base64Data.getBytes());
        String stg = new String(erg, "UTF-8");
        
         Assert.assertEquals(data, stg);
        
        
    }
    /**
     * Test of decode method, of class Base64Service.
     */
}
