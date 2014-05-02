/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.api.IConfigurationReader;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.exception.NoConfigFoundException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author richy
 */
@Component("pieShareAppConfiguration")
public class PieShareAppConfiguration implements IPieShareAppConfiguration
{

    private IConfigurationReader configurationReader;
    private Properties conf;
    private PieLogger logger = new PieLogger(PieShareAppConfiguration.class);

    @Autowired
    @Qualifier("configurationReader")
    public void setConfigurationReader(IConfigurationReader configurationReader)
    {
        this.configurationReader = configurationReader;
        try
        {
            //pieShare.properties
            conf = configurationReader.getConfig("/.pieShare/pieShare.properties");
        }
        catch (NoConfigFoundException ex)
        {
            logger.error("Cannot find pieShareAppConfig. Message: " + ex.getMessage());
        }
    }

    @Override
    public File getWorkingDirectory()
    {
        Validate.notNull(conf);

        File watchDir = new File(conf.getProperty("workingDir"));

        if (!watchDir.exists())
        {
            watchDir.mkdirs();
        }

        return new File(watchDir.getAbsolutePath());
    }

    @Override
    public int getFileSendBufferSize()
    {
        Validate.notNull(conf);

        try
        {
            return Integer.parseInt(conf.getProperty("fileSendBufferSize"));
        }
        catch (NumberFormatException ex)
        {
            return 2048;
        }
    }

    @Override
    public File getTempCopyDirectory()
    {
        Validate.notNull(conf);

        File tempCopyDir = new File(conf.getProperty("tempCopyDir"));

        if (!tempCopyDir.exists())
        {
            tempCopyDir.mkdirs();
        }

        return new File(tempCopyDir.getAbsolutePath());
    }
}
