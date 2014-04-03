/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.api;

import java.io.File;

/**
 *
 * @author Richard
 */
public interface IFileMerger
{
    public void fileCreated(File file);

    public void fileDeleted(File file);

    public void fileChanged(File file);
    
    public void setFileService(IFileService fileService);
}
