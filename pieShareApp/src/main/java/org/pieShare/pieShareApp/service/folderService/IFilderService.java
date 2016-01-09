/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;

/**
 *
 * @author daniela
 */
public interface IFilderService {
    
    
    String relativizeFilePath(File file);
    
    File getAbsolutePath(PieFilder filder);
    
    File getAbsoluteTmpPath(PieFilder filder);

    void deleteRecursive(PieFilder filder);

    
}
