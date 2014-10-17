/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author Svetoslav
 */
public class FileUtilsService implements IFileUtilsService {
    
    private IPieShareAppConfiguration pieAppConfig;
    private IBeanService beanService;
    private IHashService hashService;

    public void setPieAppConfig(IPieShareAppConfiguration pieAppConfig) {
        this.pieAppConfig = pieAppConfig;
    }

    public void setBeanService(IBeanService beanService) {
        this.beanService = beanService;
    }

    public void setHashService(IHashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public PieFile getPieFile(File file) throws FileNotFoundException, IOException {
            if (!file.exists()) {
                    throw new FileNotFoundException("File: " + file.getPath() + " does not exist");
            }

            PieFile pieFile = beanService.getBean(PieShareAppBeanNames.getPieFileName());

            Path pathBase = pieAppConfig.getWorkingDirectory().toPath();//new File(pieAppConfig.getWorkingDirectory().getAbsolutePath()).toPath();
            Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
            Path pathRelative = pathBase.relativize(pathAbsolute);
            pieFile.setRelativeFilePath(pathRelative.toString());

            pieFile.setLastModified(file.lastModified());
            pieFile.setFileName(file.getName());

            pieFile.setMd5(hashService.hashStream(new FileInputStream(file)));

            return pieFile;
    }
    
}
