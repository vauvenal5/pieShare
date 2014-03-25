/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.configuration;

import java.io.File;

/**
 *
 * @author richy
 */
public class Configuration
{

    public static File getWorkingDirectory()
    {
        File watchDir = new File("workingDir");

        if (!watchDir.exists())
        {
            watchDir.mkdirs();
        }

        return watchDir;

    }
}
