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
public class FileUtils {
    public void createFile(File file, long sizeInMB) throws InterruptedException, IOException
    {
        ProcessBuilder pb;
		
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
                //fsutil file createnew file.out 1000000000 
                //this tool needs the size in byte
                long actualSize = 1024 * 1024 * sizeInMB;
                pb = new ProcessBuilder("fsutil", "file", "createnew", file.getAbsolutePath(), String.valueOf(actualSize));
        }
        else {
                //dd if=/dev/zero of=file.out bs=1MB count=1024 
                pb = new ProcessBuilder("dd", "if=/dev/zero", "of=" + file.getAbsolutePath(), "bs=1MB", "count=" + String.valueOf(sizeInMB));
        }

        Process p = pb.start();
        p.waitFor();
    }
}
