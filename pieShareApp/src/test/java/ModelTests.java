/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.pieShare.pieShareApp.model.pieFilder.FileMeta;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ModelTests {
	
	public ModelTests() {
	}
	
	@Test
	public void PieFileEqualsTest() throws Exception {
		PieFile file = new PieFile();
		file.setDeleted(false);
		file.setName("file1");
		file.setLastModified(1000);
		file.setMd5("thisAMd5".getBytes());
		file.setRelativePath("/bla/&blad/blu");
		
		PieFile file2 = new PieFile();
		file2.setDeleted(false);
		file2.setName("file1");
		file2.setLastModified(1000);
		file2.setMd5("thisAOtherMd5".getBytes());
		file2.setRelativePath("/bla/&blad/blu");
		
		Assert.assertTrue(file.equals(file));
		Assert.assertFalse(file.equals(file2));
	}
	
	@Test
	public void FileMetaEqualsTest() throws Exception {
		
		PieFile file = new PieFile();
		file.setDeleted(false);
		file.setName("file1");
		file.setLastModified(1000);
		file.setMd5("thisAMd5".getBytes());
		file.setRelativePath("/bla/&blad/blu");
		
		FileMeta fileMeta = new FileMeta();
		fileMeta.setData("thisIsTheData".getBytes());
		fileMeta.setFile(file);
		
		Assert.assertTrue(fileMeta.equals(fileMeta));
	}
	
	@Test
	public void FileMetaInConcurrentHashMapTest() throws Exception {
		PieFile file = new PieFile();
		file.setDeleted(false);
		file.setName("file1");
		file.setLastModified(1000);
		file.setMd5("thisAMd5".getBytes());
		file.setRelativePath("/bla/&blad/blu");
		
		FileMeta fileMeta = new FileMeta();
		fileMeta.setData("thisIsTheData".getBytes());
		fileMeta.setFile(file);
		
		ConcurrentHashMap<FileMeta, Integer> map = new ConcurrentHashMap<>();
		map.put(fileMeta, 1);
		
		Assert.assertTrue(map.containsKey(fileMeta));
		Assert.assertEquals((long)1, (long)map.get(fileMeta));
	}
}
