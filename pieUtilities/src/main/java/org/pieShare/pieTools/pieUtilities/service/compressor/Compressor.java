/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class Compressor implements ICompressor
{

    private PieLogger logger = new PieLogger(Compressor.class);

    @Override
    public void compressStream(byte[] data, OutputStream out) throws IOException
    {
        Deflater deflater = new Deflater();

        deflater.setInput(data);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished())
        {
            int count = deflater.deflate(buffer); // returns the generated code... index  
            out.write(buffer, 0, count);

        }
        deflater.reset();
        deflater.end();
    }

    @Override
    public void decompressStream(byte[] data, OutputStream out) throws IOException, DataFormatException
    {
        Inflater inflater = new Inflater();

        inflater.setInput(data);

        // ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished())
        {
            int count = inflater.inflate(buffer);
            out.write(buffer, 0, count);

            if (inflater.needsInput())
            {
                break;
            }

            if (inflater.needsDictionary())
            {
                //ToDo: Check Problem with speziel chars (sonderzeichen);
            }
        }

        inflater.reset();
        inflater.end();
    }

}
