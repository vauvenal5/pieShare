/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.pieShare.pieTools.pieUtilities.service.hashService.MD5Service;

/**
 *
 * @author richy
 */
public class FileUtils
{

    public static boolean deleteRecursive(File path) throws FileNotFoundException
    {
        if (!path.exists())
        {
            throw new FileNotFoundException(path.getAbsolutePath());
        }
        boolean ret = true;
        if (path.isDirectory())
        {
            for (File f : path.listFiles())
            {
                ret = ret && FileUtils.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

}
