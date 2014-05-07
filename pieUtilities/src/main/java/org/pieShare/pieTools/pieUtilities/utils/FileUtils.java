/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	public static boolean deleteOneFile(File file) throws FileNotFoundException
	{
		if (!file.exists() || file.isDirectory())
		{
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		return file.delete();
	}

	public static void copyFileUsingStream(File source, File dest) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		try
		{
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, length);
			}
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
			if (os != null)
			{
				os.close();
			}
		}
	}

}
