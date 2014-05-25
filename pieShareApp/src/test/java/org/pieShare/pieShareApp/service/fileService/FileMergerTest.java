/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author richy
 */
public class FileMergerTest
{

    public FileMergerTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of fileCreated method, of class FileMerger. Add 3 different folders
     * to the Merger and check if they were added the right way.
     */
    @Test
    public void testFileCreatedAddNewFolders() throws Exception
    {
        PieDirectory newFolder1 = Mockito.mock(PieDirectory.class);
        Mockito.when(newFolder1.getRelativeFilePath()).thenReturn("newFolder1");

        PieDirectory newFolder2 = Mockito.mock(PieDirectory.class);
        Mockito.when(newFolder2.getRelativeFilePath()).thenReturn("newFolder2");

        PieDirectory newFolder3 = Mockito.mock(PieDirectory.class);
        Mockito.when(newFolder3.getRelativeFilePath()).thenReturn("newFolder3");

        BeanService beanService = Mockito.mock(BeanService.class);
        Mockito.when(beanService.getBean(PieDirectory.class)).thenReturn(newFolder1, newFolder2, newFolder3);

        FileMerger instance = new FileMerger();
        instance.setBeanService(beanService);

        //Check if list is empty but initalized at startup
        Assert.assertTrue(instance.getDirs().isEmpty());

        File file = Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(true);
        Mockito.when(file.isDirectory()).thenReturn(true);

        instance.fileCreated(file);

        Assert.assertEquals(1, instance.getDirs().size());
        Assert.assertTrue(instance.getDirs().containsKey("newFolder1"));
        Assert.assertFalse(instance.getDirs().containsKey("newFolder2"));
        Assert.assertFalse(instance.getDirs().containsKey("newFolder3"));

        instance.fileCreated(file);

        Assert.assertEquals(2, instance.getDirs().size());
        Assert.assertTrue(instance.getDirs().containsKey("newFolder1"));
        Assert.assertTrue(instance.getDirs().containsKey("newFolder2"));
        Assert.assertFalse(instance.getDirs().containsKey("newFolder3"));

        instance.fileCreated(file);

        Assert.assertEquals(3, instance.getDirs().size());
        Assert.assertTrue(instance.getDirs().containsKey("newFolder1"));
        Assert.assertTrue(instance.getDirs().containsKey("newFolder2"));
        Assert.assertTrue(instance.getDirs().containsKey("newFolder3"));
    }

    /**
     * Test of fileCreated method, of class FileMerger. Add a file to the Merger
     * and check if it were added the right way.
     */
    @Test
    public void testFileCreatedAddNewFiles() throws Exception
    {
        HashMap<String, PieFile> fileList = new HashMap();

        File file1 = Mockito.mock(File.class);
        Mockito.when(file1.exists()).thenReturn(true);
        Mockito.when(file1.isDirectory()).thenReturn(false);
        Mockito.when(file1.isDirectory()).thenReturn(false);
        Mockito.when(file1.getParentFile()).thenReturn(null);

        PieFile newFile1 = Mockito.mock(PieFile.class);
        Mockito.when(newFile1.getRelativeFilePath()).thenReturn("newFile1");
        Mockito.when(newFile1.getFile()).thenReturn(file1);
        Mockito.when(newFile1.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile1.getMD5()).thenReturn("SuperDuperMD5Sum");

        PieDirectory wDir = Mockito.mock(PieDirectory.class);
        Mockito.when(wDir.getRelativeFilePath()).thenReturn("workingDir");
        Mockito.when(wDir.getFiles()).thenReturn(fileList);

        FileChangedMessage message = Mockito.mock(FileChangedMessage.class);

        BeanService beanService = Mockito.mock(BeanService.class);
        Mockito.when(beanService.getBean(PieFile.class)).thenReturn(newFile1);
        Mockito.when(beanService.getBean(PieDirectory.class)).thenReturn(wDir);
        Mockito.when(beanService.getBean("fileChangedMessage")).thenReturn(message);

        FileService fileService = Mockito.mock(FileService.class);

        FileMerger instance = new FileMerger();
        instance.setBeanService(beanService);
        instance.setFileService(fileService);

        Assert.assertTrue(instance.getDirs().isEmpty());

        instance.fileCreated(file1);

        Assert.assertEquals(1, instance.getDirs().size());
        Assert.assertTrue(instance.getDirs().containsKey("workingDir"));
        Assert.assertEquals(1, fileList.size());
        Assert.assertTrue(fileList.containsKey("newFile1"));

        Mockito.verify(message, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_CREATED);
        Mockito.verify(message, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message, Mockito.times(1)).setMd5("SuperDuperMD5Sum");
        Mockito.verify(message, Mockito.times(1)).setRelativeFilePath("newFile1");

    }

    /**
     * Test of fileDeleted method, of class FileMerger.
     */
    @Test
    public void testFileDeletedDirWithFiles() throws Exception
    {
        HashMap<String, PieFile> fileList = new HashMap<>();
        
        File file = Mockito.mock(File.class);
        
        PieDirectory wDir = Mockito.mock(PieDirectory.class);
        Mockito.when(wDir.getRelativeFilePath()).thenReturn("folder1");
        Mockito.when(wDir.getFiles()).thenReturn(fileList);
        
         PieFile wdirFile = Mockito.mock(PieFile.class);
        Mockito.when(wdirFile.getRelativeFilePath()).thenReturn("folder1");

        PieFile newFile1 = Mockito.mock(PieFile.class);
        Mockito.when(newFile1.getRelativeFilePath()).thenReturn("folder1/newFile1");
        Mockito.when(newFile1.getFile()).thenReturn(file);
        Mockito.when(newFile1.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile1.getMD5()).thenReturn("SuperDuperMD5Sum");
        
        PieFile newFile2 = Mockito.mock(PieFile.class);
        Mockito.when(newFile2.getRelativeFilePath()).thenReturn("folder1/newFile2");
        Mockito.when(newFile2.getFile()).thenReturn(file);
        Mockito.when(newFile2.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile2.getMD5()).thenReturn("SuperDuperMD5Sum");

        fileList.put("folder1/newFile1", newFile1);
        fileList.put("folder1/newFile2", newFile2);

        FileChangedMessage message = Mockito.mock(FileChangedMessage.class);
        FileChangedMessage message2 = Mockito.mock(FileChangedMessage.class);
        
        BeanService beanService = Mockito.mock(BeanService.class);
        Mockito.when(beanService.getBean(PieFile.class)).thenReturn(wdirFile);
        Mockito.when(beanService.getBean(PieDirectory.class)).thenReturn(wDir);
        Mockito.when(beanService.getBean("fileChangedMessage")).thenReturn(message, message2);
        
        FileService fileService = Mockito.mock(FileService.class);

        FileMerger instance = new FileMerger();
        instance.setBeanService(beanService);
        instance.setFileService(fileService);

        instance.getDirs().put("folder1", wDir);

        instance.fileDeleted(file);
        
        Mockito.verify(message, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_DELETED);
        Mockito.verify(message, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message, Mockito.times(1)).setMd5("SuperDuperMD5Sum");
        Mockito.verify(message, Mockito.times(1)).setRelativeFilePath("folder1/newFile1");
        
        Mockito.verify(message2, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_DELETED);
        Mockito.verify(message2, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message2, Mockito.times(1)).setMd5("SuperDuperMD5Sum");
        Mockito.verify(message2, Mockito.times(1)).setRelativeFilePath("folder1/newFile2");
    }

    
    
    
    @Test
    public void testFileDeletedFiles() throws Exception
    {
        HashMap<String, PieFile> fileList = new HashMap<>();
        
        File file = Mockito.mock(File.class);
        
        PieDirectory wDir = Mockito.mock(PieDirectory.class);
        Mockito.when(wDir.getRelativeFilePath()).thenReturn("workingDir");
        Mockito.when(wDir.getFiles()).thenReturn(fileList);
        
        PieFile newFile1 = Mockito.mock(PieFile.class);
        Mockito.when(newFile1.getRelativeFilePath()).thenReturn("workingDir/newFile1");
        Mockito.when(newFile1.getFile()).thenReturn(file);
        Mockito.when(newFile1.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile1.getMD5()).thenReturn("SuperDuperMD5Sum");
        
        PieFile newFile2 = Mockito.mock(PieFile.class);
        Mockito.when(newFile2.getRelativeFilePath()).thenReturn("workingDir/newFile2");
        Mockito.when(newFile2.getFile()).thenReturn(file);
        Mockito.when(newFile2.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile2.getMD5()).thenReturn("SuperDuperMD5Sum");

        fileList.put("workingDir/newFile1", newFile1);
        fileList.put("workingDir/newFile2", newFile2);

        FileChangedMessage message = Mockito.mock(FileChangedMessage.class);
        FileChangedMessage message2 = Mockito.mock(FileChangedMessage.class);
        
        BeanService beanService = Mockito.mock(BeanService.class);
        Mockito.when(beanService.getBean(PieFile.class)).thenReturn(newFile1, newFile2);
        Mockito.when(beanService.getBean(PieDirectory.class)).thenReturn(wDir);
        Mockito.when(beanService.getBean("fileChangedMessage")).thenReturn(message, message2);
        
        FileService fileService = Mockito.mock(FileService.class);

        FileMerger instance = new FileMerger();
        instance.setBeanService(beanService);
        instance.setFileService(fileService);

        instance.getDirs().put("workingDir", wDir);

        //Delete First file
        instance.fileDeleted(file);
        
        //Delete Second File.
        instance.fileDeleted(file);
        
        //Folder should now also be deleted
        Assert.assertTrue(instance.getDirs().isEmpty());
        
        Mockito.verify(message, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_DELETED);
        Mockito.verify(message, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message, Mockito.times(1)).setMd5("SuperDuperMD5Sum");
        Mockito.verify(message, Mockito.times(1)).setRelativeFilePath("workingDir/newFile1");
        
        Mockito.verify(message2, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_DELETED);
        Mockito.verify(message2, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message2, Mockito.times(1)).setMd5("SuperDuperMD5Sum");
        Mockito.verify(message2, Mockito.times(1)).setRelativeFilePath("workingDir/newFile2");
    }

    
    
    @Test
    public void testFileChangedOneFile() throws Exception
    {
        HashMap<String, PieFile> fileList = new HashMap<>();
        
        File file = Mockito.mock(File.class);
        //Mockito.when(file.getParentFile()).then
        
        PieDirectory wDir = Mockito.mock(PieDirectory.class);
        Mockito.when(wDir.getRelativeFilePath()).thenReturn("workingDir");
        Mockito.when(wDir.getFiles()).thenReturn(fileList);
        
        PieFile newFile1 = Mockito.mock(PieFile.class);
        Mockito.when(newFile1.getRelativeFilePath()).thenReturn("workingDir/newFile1");
        Mockito.when(newFile1.getFile()).thenReturn(file);
        Mockito.when(newFile1.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile1.getMD5()).thenReturn("SuperDuperMD5Sum");
        
        PieFile newFile2 = Mockito.mock(PieFile.class);
        Mockito.when(newFile2.getRelativeFilePath()).thenReturn("workingDir/newFile2");
        Mockito.when(newFile2.getFile()).thenReturn(file);
        Mockito.when(newFile2.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile2.getMD5()).thenReturn("SuperDuperMD5Sum");

        
        PieFile newFile3 = Mockito.mock(PieFile.class);
        Mockito.when(newFile3.getRelativeFilePath()).thenReturn("workingDir/newFile1");
        Mockito.when(newFile3.getFile()).thenReturn(file);
        Mockito.when(newFile3.getLastModified()).thenReturn((long) 10);
        //Mockito.when(newFile3.getMD5()).thenReturn("NewMD5");

        
        fileList.put("workingDir/newFile1", newFile1);
        fileList.put("workingDir/newFile2", newFile2);

        FileChangedMessage message = Mockito.mock(FileChangedMessage.class);
        
        BeanService beanService = Mockito.mock(BeanService.class);
        Mockito.when(beanService.getBean(PieFile.class)).thenReturn(newFile3);
        Mockito.when(beanService.getBean(PieDirectory.class)).thenReturn(wDir);
        Mockito.when(beanService.getBean("fileChangedMessage")).thenReturn(message);
        
        FileService fileService = Mockito.mock(FileService.class);

        FileMerger instance = new FileMerger();
        instance.setBeanService(beanService);
        instance.setFileService(fileService);

        instance.getDirs().put("workingDir", wDir);

        
        instance.fileChanged(file);
        
        Mockito.verify(message, Mockito.times(1)).setChangedType(FileChangedTypes.FILE_MODIFIED);
        Mockito.verify(message, Mockito.times(1)).setLastModified((long) 10);
        //Mockito.verify(message, Mockito.times(1)).setMd5("NewMD5");
        Mockito.verify(message, Mockito.times(1)).setRelativeFilePath("workingDir/newFile1");
        
        Assert.assertEquals(newFile3, wDir.getFiles().get("workingDir/newFile1"));
    }
    

    /**
     * Test of remoteFileChanged method, of class FileMerger.
     */
    public void testRemoteFileChanged() throws Exception
    {
        System.out.println("remoteFileChanged");
        FileChangedMessage fileChangedMessage = null;
        FileMerger instance = new FileMerger();
        instance.remoteFileChanged(fileChangedMessage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
