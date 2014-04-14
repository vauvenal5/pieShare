/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

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
    public void testCompressStream() throws Exception
    {
        String data = "Zu Tyonis dem T?rannen, schlich Damon, den Dolche im gewande. Ihn schlugen die H?scher in Bande. Was wolltest du mit dem Dolche, Sprich!";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
        OutputStream out = new ByteArrayOutputStream();
        Compressor instance = new Compressor();
        instance.compressStream(in, out);

        byte[] byteResult = ((ByteArrayOutputStream) out).toByteArray();
        String erg = new String(byteResult);
        
        InputStream inDe = new ByteArrayInputStream(byteResult);
        OutputStream outDe = new ByteArrayOutputStream();

        instance.decompressStream(inDe, outDe);
        String decErg = new String(((ByteArrayOutputStream) outDe).toByteArray(), "UTF-8");

        Assert.assertEquals(data, decErg);
        Assert.assertTrue(data.length() > erg.length());
    }

    /**
     * Test of decompressStream method, of class Compressor.
     */
    public void testDecompressStream() throws Exception
    {
        System.out.println("decompressStream");
        InputStream in = null;
        OutputStream out = null;
        Compressor instance = new Compressor();
        instance.decompressStream(in, out);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
