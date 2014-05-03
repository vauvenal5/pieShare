/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.Mockito;
import static org.mockito.Mockito.doAnswer;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;

/**
 *
 * @author richy
 */
public class CompressorTest
{

    public CompressorTest()
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
     * Test of compressStream method, of class Compressor.
     */
    @Test
    public void testCompressorByteArrayDecode() throws Exception
    {
        String data = "Zu Tyonis dem Türannen, schlich Damon, den Dolche im gewande. Ihn schlugen die Häscher in Bande. Was wolltest du mit dem Dolche, Sprich!";
        
        Compressor instance = new Compressor();
        
        IBase64Service base64Service = Mockito.mock(IBase64Service.class);
        
        doAnswer(returnsFirstArg()).when(base64Service).decode(Mockito.any(byte[].class));
        doAnswer(returnsFirstArg()).when(base64Service).encode(Mockito.any(byte[].class));
        
        instance.setBase64Service(base64Service);
        
        byte[] out = instance.compressByteArray(data.getBytes("UTF-8"), data.length());

        //String erg = new String(out);
        
        byte[] decText = instance.decompressByteArray(out, out.length);
        String decErg = new String(decText, "UTF-8");
        
        Assert.assertEquals(data, decErg);
        //<Assert.assertTrue(data.length() > erg.length());
    }
}
