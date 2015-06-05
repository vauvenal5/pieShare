/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTestTools;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author vauvenal5
 */
public class TestFileUtils {
    public static void createFile(File file, long sizeInMB) throws InterruptedException, IOException
    {
        ProcessBuilder pb;
		
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
                //this tool needs the size in byte
                long actualSize = 1024 * 1024 * sizeInMB;
                pb = new ProcessBuilder("fsutil", "file", "createnew", file.getAbsolutePath(), String.valueOf(actualSize));
        }
        else {
                String count = "count=" + String.valueOf(sizeInMB);
                pb = new ProcessBuilder("dd", "if=/dev/zero", "of=" + file.getAbsolutePath(), "bs=1MB", count);
        }

        Process p = pb.start();
        p.waitFor();
    }
}
