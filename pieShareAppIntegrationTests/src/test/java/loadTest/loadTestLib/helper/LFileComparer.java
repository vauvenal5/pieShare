/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.helper;

import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;

/**
 *
 * @author richy
 */
public class LFileComparer {

    private boolean result = true;
    private ILocalFileCompareService fileCompareService;

    public LFileComparer() {
    }

    public void setFileCompareService(ILocalFileCompareService fileCompareService) {
        this.fileCompareService = fileCompareService;
    }

    public synchronized void comarare(List<PieFile> files) {
        result = result && files.stream().allMatch(pf -> fileCompareService.equalsWithLocalPieFile(pf));
    }

    public synchronized boolean getResult() {
        return result;
    }
}
