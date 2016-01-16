/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.database.DatabaseService;
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

    @Test
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

        PieFile fileFromDB = dbService.findPieFile(file);

        Assert.assertEquals(file, fileFromDB);
    }

    /**
     * This test tries to persist a newly created PieFolder to the DB. When
     * retrieved again it should be the same.
     */
    @Test //Should_ExpectedBehavior_When_StateUnderTest
    public void Should_PersistFolderInDB_When_FolderAddedToDB() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFolder folder = new PieFolder();
        folder.setName("testFolder");
        folder.setRelativePath("testFolder");
        dbService.persistPieFolder(folder);

        PieFolder folderFromDB = dbService.findPieFolder(folder);

        Assert.assertEquals(folder.getRelativePath(), folderFromDB.getRelativePath());

        dbService.removePieFolder(folder);

        Assert.assertNull(dbService.findPieFolder(folder));
    }

    /**
     * A newly created PieFolder is persisted to the DB, then changed and the
     * changes are also merged into the DB. When retrieved the PieFolders
     * relative path (including the name) should equal the updated ones.
     */
    @Test
    public void Should_UpdateFolderInDB_When_ChangedFolderMergedInDB() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFolder folder = new PieFolder();
        folder.setName("oldFolder");
        folder.setRelativePath("oldFolder");
        dbService.persistPieFolder(folder);

        folder.setName("newFolder");
        folder.setRelativePath("newFolder");
        dbService.mergePieFolder(folder);

        PieFolder folderFromDB = dbService.findPieFolder(folder);

        Assert.assertEquals(folder.getRelativePath(), folderFromDB.getRelativePath());

        dbService.removePieFolder(folder);

        Assert.assertNull(dbService.findPieFolder(folder));
    }

    /**
     * Different PieFolders are persisted in the DB. All Folders should be
     * returned with the method findAllPieFolders and equal the persisted ones.
     */
    @Test
    public void Should_FindAllPieFoldersInDB_When_FoldersPersistedInDB() {
        DatabaseService dbService = this.context.getBean(DatabaseService.class);

        PieFolder folder1 = new PieFolder();
        folder1.setName("folder1");
        folder1.setRelativePath("folder1");
        dbService.persistPieFolder(folder1);

        PieFolder folder2 = new PieFolder();
        folder2.setName("folder2");
        folder2.setRelativePath("folder2");
        dbService.persistPieFolder(folder2);

        PieFolder folder3 = new PieFolder();
        folder3.setName("folder3");
        folder3.setRelativePath("folder3");
        dbService.persistPieFolder(folder3);

        List<PieFolder> folders = dbService.findAllPieFolders();

        Assert.assertTrue(folders.contains(folder1));
        Assert.assertTrue(folders.contains(folder2));
        Assert.assertTrue(folders.contains(folder3));

        Assert.assertTrue(folders.size() == 3);

        for (PieFolder f : folders) {
            dbService.removePieFolder(f);
        }
    }

    @Test
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

        dbService.persistPieFile(file);
        dbService.persistPieFile(file1);
        dbService.persistPieFile(file2);

        List<PieFile> filesFromDB = dbService.findPieFileByHash(file);

        Assert.assertEquals(filesFromDB.size(), 2);
        Assert.assertEquals(filesFromDB.get(0).getMd5(), "test".getBytes());
    }
}
