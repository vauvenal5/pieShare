/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.service.fileService.api.IFilderIterationCallback;

/**
 *
 * @author daniela
 */
public interface IFilderService {
    
    
    /**
     * Get the relative path of a java File object (includes folder)
     * starting at the working directory.
     * @param file to get the relative path of
     * @return the relative path
     */
    String relativizeFilePath(File file);
    
    /**
     * Gets the absolute path of a PieFilder object (PieFolder and PieFile)
     * @param filder to get the absolute path from
     * @return A java file
     */
    File getAbsolutePath(PieFilder filder);
    
    /**
     * Gets the absolute path of a PieFilder object (PieFolder and PieFile) in the tmp dir.
     * @param filder to get the absolute path from
     * @return A java file
     */
    File getAbsoluteTmpPath(PieFilder filder);

    /**
     * Delete a PieFilder (PieFolder and PieFile) locally, and all its content.
     * @param filder to delete
     */
    void deleteRecursive(PieFilder filder);

    /**
     * Delete a java File object locally, and all its content.
     * @param file to delete
     */
    void deleteRecursive(File file);
}
