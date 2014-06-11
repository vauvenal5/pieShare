/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.tempFolderService.api;

import java.io.File;

/**
 *
 * @author Richard
 */
public interface ITempFolderService
{
    public File createTempFolder(String fileName, File parentDir) throws Exception;
}
