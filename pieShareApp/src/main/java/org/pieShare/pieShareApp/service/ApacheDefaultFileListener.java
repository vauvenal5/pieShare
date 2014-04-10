/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.pieShare.pieShareApp.api.IFileMerger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements FileListener
{

    private IFileMerger fileMerger;
    
    public void setFileMerger(IFileMerger fileMerger)
    {
        this.fileMerger = fileMerger;
    }
    
    @Override
    public void fileCreated(FileChangeEvent fce) throws Exception
    {
        String filePath = fce.getFile().getURL().getFile();
        fileMerger.fileCreated(new File(filePath));
    }

    @Override
    public void fileDeleted(FileChangeEvent fce) throws Exception
    {
        String filePath = fce.getFile().getURL().getFile();
        fileMerger.fileDeleted(new File(filePath));
    }

    @Override
    public void fileChanged(FileChangeEvent fce) throws Exception
    {
        String filePath = fce.getFile().getURL().getFile();
        fileMerger.fileChanged(new File(filePath));
    }

}
