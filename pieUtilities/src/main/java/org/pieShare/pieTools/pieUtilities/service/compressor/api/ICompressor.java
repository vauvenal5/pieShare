/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author richy
 */
public interface ICompressor extends IPieTask
{

    public void setNewCompressJob(InputStream in, OutputStream out) throws IOException;

    public void setNewDeCompressJob(InputStream in, OutputStream out) throws IOException;

    public void compressStream(InputStream in, OutputStream out) throws IOException;

    public void decompressStream(InputStream in, OutputStream out) throws IOException, DataFormatException;
}
