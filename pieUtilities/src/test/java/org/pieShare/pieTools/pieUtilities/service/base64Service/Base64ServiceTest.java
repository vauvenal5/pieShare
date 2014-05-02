/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.base64Service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
    
    public void testEncode()
    {
        String data = "Zu Tyonis dem Türannen, schlich Damon, den Dolche im gewande. Ihn schlugen die Häscher in Bande. Was wolltest du mit dem Dolche, Sprich!";
        String base64Data = "WnUgVHlvbmlzIGRlbSBUP3Jhbm5lbiwgc2NobGljaCBEYW1vbiwgZGVuIERvbGNoZSBpbSBnZXdhbmRlLiBJaG4gc2NobHVnZW4gZGllIEg/c2NoZXIgaW4gQmFuZGUuIFdhcyB3b2xsdGVzdCBkdSBtaXQgZGVtIERvbGNoZSwgU3ByaWNoIQ==";

        IBase64Service service = new Base64Service();

        byte[] erg = service.encode(data.getBytes());
        String stg = new String(erg);

        Assert.assertEquals(base64Data, stg);
    }
    
    public void testDecode()
    {
        String data = "Zu Tyonis dem Türannen, schlich Damon, den Dolche im gewande. Ihn schlugen die Häscher in Bande. Was wolltest du mit dem Dolche, Sprich!";
        String base64Data = "WnUgVHlvbmlzIGRlbSBU/HJhbm5lbiwgc2NobGljaCBEYW1vbiwgZGVuIERvbGNoZSBpbSBnZXdhbmRlLiBJaG4gc2NobHVnZW4gZGllIEjkc2NoZXIgaW4gQmFuZGUuIFdhcyB3b2xsdGVzdCBkdSBtaXQgZGVtIERvbGNoZSwgU3ByaWNoIQ==";
        IBase64Service service = new Base64Service();

        byte[] erg = service.decode(base64Data.getBytes());
        String stg = new String(erg);

        Assert.assertEquals(data, stg);
    }

    /**
     * Test of decode method, of class Base64Service.
     */
}
