/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class Compressor implements ICompressor
{

    private final PieLogger logger = new PieLogger(Compressor.class);
    private IBase64Service base64Service;

    public void setBase64Service(IBase64Service base64Service)
    {
        this.base64Service = base64Service;
    }

    
    @Override
    public byte[] compressByteArray(byte[] data) throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        Deflater deflater = new Deflater();

        deflater.setInput(base64Service.encode(data));

        
        deflater.finish();
        byte[] buffer = new byte[data.length];
        while (!deflater.finished())
        {
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outStream.write(buffer, 0, count);

        }
        deflater.reset();
        deflater.end();

        return base64Service.encode(outStream.toByteArray());

    }

    @Override
    public byte[] decompressByteArray(byte[] data) throws IOException, DataFormatException
    {
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        Inflater inflater = new Inflater();

        inflater.setInput(base64Service.decode(data));

        // ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[data.length];
        while (!inflater.finished())
        {
            int count = inflater.inflate(buffer);
            outStream.write(buffer, 0, count);

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
        
        return base64Service.decode(outStream.toByteArray());
                
    }

}
