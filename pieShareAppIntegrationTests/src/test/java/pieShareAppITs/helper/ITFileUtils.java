/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Svetoslav
 */
public class ITFileUtils {
	
	public static File createFile(File file, int size) throws FileNotFoundException, IOException {
		int defaultSize = 1024;
		if(size < defaultSize) {
			defaultSize = size;
		}
		
		int max = size / defaultSize;
		int delta = size - (max * defaultSize);
		Random random = new Random();
		byte[] data = new byte[defaultSize];
		
		for(int i = 0; i<max; i++) {
			random.nextBytes(data);
			FileUtils.writeByteArrayToFile(file, data, true);
		}
		
		data = new byte[delta];
		random.nextBytes(data);
		FileUtils.writeByteArrayToFile(file, data, true);
		
		return file;
	}
}
