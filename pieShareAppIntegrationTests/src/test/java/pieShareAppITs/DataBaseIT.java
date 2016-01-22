/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.fileService.LocalFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITUtil;

/**
 *
 * @author Svetoslav
 */
public class DataBaseIT {

    private AnnotationConfigApplicationContext context;

    public DataBaseIT() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ITUtil.performTearDownDelete();
        ITUtil.setUpEnviroment(true);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        context = ITUtil.getContext();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        ITUtil.performTearDown(context);
    }

    //@Test
    public void persistPieFile() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFile file = new PieFile();
        file.setDeleted(false);
        file.setName("testFile");
        file.setLastModified(0);
        file.setMd5("test".getBytes());
        file.setRelativePath("testFolder");

        dbService.persistPieFile(file);
        file.setName("testFile2");
        dbService.mergePieFile(file);

        PieFile fileFromDB = dbService.findPieFileByRelativeFilePath(file);

        Assert.assertEquals(file, fileFromDB);
    }

    /**
     * This test tries to persist a newly created PieFolder to the DB. When
     * retrieved again it should be the same.
     */
   // @Test //Should_ExpectedBehavior_When_StateUnderTest
    public void Should_PersistFolderInDB_When_FolderAddedToDB() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFolder folder = new PieFolder();
        folder.setName("testFolder");
        folder.setRelativePath("testFolder");
        dbService.persistPieFolder(folder);

        PieFolder folderFromDB = dbService.findPieFolderByRelativeFilePath(folder);

        Assert.assertEquals(folder.getRelativePath(), folderFromDB.getRelativePath());

        dbService.removePieFolder(folder);

        Assert.assertNull(dbService.findPieFolderByRelativeFilePath(folder));
    }

    /**
     * A newly created PieFolder is persisted to the DB, then changed and the
     * changes are also merged into the DB. When retrieved the PieFolders
     * relative path (including the name) should equal the updated ones.
     */
    //@Test
    public void Should_UpdateFolderInDB_When_ChangedFolderMergedInDB() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFolder folder = new PieFolder();
        folder.setName("oldFolder");
        folder.setRelativePath("oldFolder");
        dbService.persistPieFolder(folder);

        folder.setName("newFolder");
        folder.setRelativePath("newFolder");
        dbService.mergePieFolder(folder);

        PieFolder folderFromDB = dbService.findPieFolderByRelativeFilePath(folder);

        Assert.assertEquals(folder.getRelativePath(), folderFromDB.getRelativePath());

        dbService.removePieFolder(folder);

        Assert.assertNull(dbService.findPieFolderByRelativeFilePath(folder));
    }

    /**
     * Different PieFolders are persisted in the DB. All Folders should be
     * returned with the method findAllPieFolders and equal the persisted ones.
     */
    @Test
    public void Should_FindAllPieFoldersInDB_When_FoldersPersistedInDB_SameWithFiles() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);
        IUserService userService = this.context.getBean(IUserService.class);
        IFileService fileService = this.context.getBean(LocalFileService.class);
        IFolderService folderService = this.context.getBean(IFolderService.class);

        File testWorkingDir = userService.getUser().getPieShareConfiguration().getWorkingDir();

        File dir1 = new File(testWorkingDir, "dir1");
        if (!dir1.mkdirs()) {
            Assert.fail("Could not create directories");
        }

        PieFolder pieFolder1 = folderService.getPieFolder(dir1);

        PieFile pieFile1 = null;
        File file1 = new File(dir1, "file1");
        try {
            FileUtils.writeByteArrayToFile(file1, "TestContent".getBytes());
            pieFile1 = fileService.getPieFile(file1);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        File dir2 = new File(dir1, "dir2");
        if (!dir2.mkdirs()) {
            Assert.fail("Could not create directories");
        }
        PieFolder pieFolder2 = folderService.getPieFolder(dir2);

        PieFile pieFile2 = null;
        File file2 = new File(dir1, "file2");
        try {
            FileUtils.writeByteArrayToFile(file2, "TestContent".getBytes());
            pieFile2 = fileService.getPieFile(file2);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        PieFile pieFile3 = null;
        File file3 = new File(dir2, "file3");
        try {
            FileUtils.writeByteArrayToFile(file3, "TestContent".getBytes());
            pieFile3 = fileService.getPieFile(file3);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        PieFile pieFile4 = null;
        File file4 = new File(testWorkingDir, "file4");
        try {
            FileUtils.writeByteArrayToFile(file4, "TestContent".getBytes());
            pieFile4 = fileService.getPieFile(file4);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        dbService.persistPieFile(pieFile1);
        dbService.persistPieFolder(pieFolder1);
        dbService.persistPieFile(pieFile3);
        dbService.persistPieFile(pieFile4);

        PieFile pieFile1FromDB = dbService.findPieFileByRelativeFilePath(pieFile1);
        PieFile pieFile3FromDB = dbService.findPieFileByRelativeFilePath(pieFile3);
        PieFile pieFile4FromDB = dbService.findPieFileByRelativeFilePath(pieFile4);

        File fileFromDB = new File(userService.getUser().getPieShareConfiguration().getWorkingDir(), pieFile1FromDB.getRelativePath());
        File file3FromDB = new File(userService.getUser().getPieShareConfiguration().getWorkingDir(), pieFile3FromDB.getRelativePath());
        File file4FromDB = new File(userService.getUser().getPieShareConfiguration().getWorkingDir(), pieFile4FromDB.getRelativePath());

        try {
            String canFromDb = fileFromDB.getCanonicalPath();
            String canFile1 = file1.getCanonicalPath();
            Assert.assertEquals(canFile1, canFromDb);

            Assert.assertEquals(file3.getCanonicalPath(), file3FromDB.getCanonicalPath());
            Assert.assertEquals(file4.getCanonicalPath(), file4FromDB.getCanonicalPath());

        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertEquals(pieFile1.getName(), pieFile1FromDB.getName());
        Assert.assertEquals(pieFile3.getName(), pieFile3FromDB.getName());
        Assert.assertEquals(pieFile4.getName(), pieFile4FromDB.getName());
    }

    /**
     * Different PieFolders and pieFiles are persisted in the DB and read back
     * from the DB at Once.
     */
    //@Test
    public void TestPersisFilesAndFolder_LoadAllFilesAndFolderAtOnce() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);
        IUserService userService = this.context.getBean(IUserService.class);
        IFileService fileService = this.context.getBean(LocalFileService.class);
        IFolderService folderService = this.context.getBean(IFolderService.class);

        File testWorkingDir = userService.getUser().getPieShareConfiguration().getWorkingDir();

        File dir1 = new File(testWorkingDir, "dir1");
        if (!dir1.mkdirs()) {
            Assert.fail("Could not create directories");
        }

        PieFolder pieFolder1 = folderService.getPieFolder(dir1);

        PieFile pieFile1 = null;
        File file1 = new File(dir1, "file1");
        try {
            FileUtils.writeByteArrayToFile(file1, "TestContent".getBytes());
            pieFile1 = fileService.getPieFile(file1);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        File dir2 = new File(dir1, "dir2");
        if (!dir2.mkdirs()) {
            Assert.fail("Could not create directories");
        }
        PieFolder pieFolder2 = folderService.getPieFolder(dir2);

        PieFile pieFile2 = null;
        File file2 = new File(dir1, "file2");
        try {
            FileUtils.writeByteArrayToFile(file2, "TestContent".getBytes());
            pieFile2 = fileService.getPieFile(file2);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        PieFile pieFile3 = null;
        File file3 = new File(dir2, "file3");
        try {
            FileUtils.writeByteArrayToFile(file3, "TestContent".getBytes());
            pieFile3 = fileService.getPieFile(file3);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        PieFile pieFile4 = null;
        File file4 = new File(testWorkingDir, "file4");
        try {
            FileUtils.writeByteArrayToFile(file4, "TestContent".getBytes());
            pieFile4 = fileService.getPieFile(file4);
        } catch (IOException ex) {
            Assert.fail("Could not create file");
        }

        dbService.persistPieFile(pieFile1);
        dbService.persistPieFolder(pieFolder1);
        dbService.persistPieFile(pieFile3);
        dbService.persistPieFile(pieFile4);

        List<PieFile> allFiles = dbService.findAllPieFiles();
        List<PieFolder> folders = dbService.findAllPieFolders();

        Assert.assertEquals(3, allFiles.size());
        Assert.assertEquals(2, folders.size());
    }

    //@Test
    public void testFindAllFilesByHash() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFile file = new PieFile();
        file.setDeleted(false);
        file.setName("testFile");
        file.setLastModified(0);
        file.setMd5("test".getBytes());
        file.setRelativePath("testFolder");

        PieFile file1 = new PieFile();
        file1.setDeleted(false);
        file1.setName("testFile2");
        file1.setLastModified(10);
        file1.setMd5("test".getBytes());
        file1.setRelativePath("testFolder2");

        PieFile file2 = new PieFile();
        file2.setDeleted(false);
        file2.setName("testFile3");
        file2.setLastModified(0);
        file2.setMd5("testNotSameHash".getBytes());
        file2.setRelativePath("testFolder3");

        //todo-mr3-richy: this statement throws an exception; test works due to luck
        dbService.persistPieFile(file);
        dbService.persistPieFile(file1);
        dbService.persistPieFile(file2);

        List<PieFile> filesFromDB = dbService.findPieFileByHash(file.getMd5());

        Assert.assertEquals(filesFromDB.size(), 2);
        Assert.assertEquals(filesFromDB.get(0).getMd5(), "test".getBytes());
    }
}
