/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import org.pieShare.pieShareApp.model.pieFile.PieFolder;

/**
 *
 * @author daniela
 */
public interface IFolderService {
    void createFolder(PieFolder pieFolder) throws FolderServiceException;
}
