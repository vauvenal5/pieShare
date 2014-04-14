/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class Compressor implements ICompressor
{

    private PieLogger logger = new PieLogger(Compressor.class);
    private boolean isCompressJob = true;
    private InputStream in;
    private OutputStream out;

    @Override
    public void compressStream(InputStream in, OutputStream out) throws IOException
    {
        byte[] buff = new byte[1024];
        Deflater deflater = new Deflater();
        int readBytes = 0;

        while ((readBytes = in.read(buff)) != -1)
        {
            deflater.setInput(buff, 0, readBytes);

            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished())
            {
                int count = deflater.deflate(buffer); // returns the generated code... index  
                out.write(buffer, 0, count);
            }

        }
        deflater.end();
    }

    @Override
    public void decompressStream(InputStream in, OutputStream out) throws IOException, DataFormatException
    {
        byte[] buff = new byte[1024];
        int readBytes = 0;

        Inflater inflater = new Inflater();
        while ((readBytes = in.read(buff)) != -1)
        {
            inflater.setInput(buff, 0, readBytes);

            // ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[1024];
            while (!inflater.finished())
            {
                if (inflater.needsInput())
                {
                    break;
                }

                int count = inflater.inflate(buffer);
                out.write(buffer, 0, count);

                if (inflater.needsDictionary())
                {
                    //ToDo: Check Problem with speziel chars (sonderzeichen);
                }
            }
        }
        inflater.end();
    }

    @Override
    public void run()
    {
        Validate.notNull(out);
        Validate.notNull(in);

        try
        {
            if (isCompressJob)
            {
                compressStream(in, out);
            }
            else
            {
                decompressStream(in, out);
            }
        }
        catch (IOException | DataFormatException ex)
        {
            logger.error("Error while compressing. Message: " + ex.getMessage());
        }
    }

    @Override
    public void setNewCompressJob(InputStream in, OutputStream out) throws IOException
    {
        Validate.notNull(out);
        Validate.notNull(in);

        in.available();

        isCompressJob = true;
        this.in = in;
        this.out = out;
    }

    @Override
    public void setNewDeCompressJob(InputStream in, OutputStream out) throws IOException
    {
        Validate.notNull(out);
        Validate.notNull(in);

        in.available();

        isCompressJob = false;
        this.in = in;
        this.out = out;
    }
}
