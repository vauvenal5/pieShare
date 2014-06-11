/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.fileUtileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Richard
 */
public interface IFileUtileService
{

    public boolean deleteRecursive(File path) throws FileNotFoundException;

    public boolean deleteOneFile(File file) throws FileNotFoundException;

    public void copyFileUsingStream(File source, File dest) throws IOException;
}
