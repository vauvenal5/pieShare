/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.compressor.api;

import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 *
 * @author richy
 */
public interface ICompressor
{

    public byte[] compressByteArray(byte[] data) throws IOException;

    public byte[] decompressByteArray(byte[] data) throws IOException, DataFormatException;
}
