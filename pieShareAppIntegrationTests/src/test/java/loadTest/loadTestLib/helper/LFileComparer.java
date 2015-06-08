/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.helper;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author richy
 */
public class LFileComparer {

    private PieFile file;
    private boolean result = true;

    public LFileComparer() {
    }

    public void setFile(PieFile file) {
        this.file = file;
    }
    
    public synchronized void comarare(List<PieFile> files) {
       result = result && files.stream().allMatch(f -> f.equals(file));
    }

    public synchronized boolean getResult() {
        return result;
    }
}
