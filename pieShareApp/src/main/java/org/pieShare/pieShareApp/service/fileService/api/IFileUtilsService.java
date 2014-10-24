/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IFileUtilsService {
    public PieFile getPieFile(File file) throws FileNotFoundException, IOException;
}
