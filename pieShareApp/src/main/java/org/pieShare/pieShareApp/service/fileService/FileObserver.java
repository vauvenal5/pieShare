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
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileObserver;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author Richard
 */
public class FileObserver implements IFileObserver
{

    private IFileMerger fileMerger;
    private File file;
    private FileChangedTypes event;
    private final long TIME_OUT_SEC = 60 * 60;

    public void setFileMerger(IFileMerger fileMerger)
    {
        this.fileMerger = fileMerger;
    }

    @Override
    public void setData(File file, FileChangedTypes event)
    {
        this.file = file;
        this.event = event;
    }

    @Override
    public void run()
    {

        if (event == FileChangedTypes.FILE_DELETED)
        {
            fileMerger.fileDeleted(file);
            return;
        }

        FileInputStream st;

        boolean isCopying = true;

        while (isCopying)
        {

            try
            {
                st = new FileInputStream(file);
                isCopying = false;
                st.close();
            }
            catch (FileNotFoundException ex)
            {

            }
            catch (IOException ex)
            {

            }
        }

        if (event == FileChangedTypes.FILE_CREATED)
        {
            fileMerger.fileCreated(file);
        }
        else if (event == FileChangedTypes.FILE_MODIFIED)
        {
            fileMerger.fileChanged(file);
        }
    }

}
