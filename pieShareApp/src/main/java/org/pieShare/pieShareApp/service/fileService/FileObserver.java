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
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileObserver;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author Richard
 */
public class FileObserver implements IFileObserver
{

    private IShareService shareService;
    private File file;
    private FileChangedTypes event;
    private IBeanService beanService;
    private final long TIME_OUT_SEC = 60 * 60;

    public void setShareService(IShareService shareService)
    {
        this.shareService = shareService;
    }

    @Override
    public void setData(File file, FileChangedTypes event)
    {
        this.file = file;
        this.event = event;
    }
    
    public void setBeanService(IBeanService beanService)
    {
	this.beanService = beanService;
    }

    @Override
    public void run()
    {

        if (!(event == FileChangedTypes.FILE_CREATED || event == FileChangedTypes.FILE_MODIFIED))
        {
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
	    PieFile pieFile = beanService.getBean(PieShareAppBeanNames.getPieFileName());
	    pieFile.Init(file);
	    
            shareService.shareFile(pieFile);
        }
        /*else if (event == FileChangedTypes.FILE_MODIFIED)
        {
            fileMerger.fileChanged(file);
        }*/
    }

}
