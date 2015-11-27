/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.message;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieTools.piePlate.model.message.AClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPiePlainTextMessage;

/**
 *
 * @author richy
 */
public class AllFilesCompleteMessage extends AClusterMessage implements IPiePlainTextMessage {
    
    private List<PieFile> files;

    public List<PieFile> getFiles() {
        return files;
    }

    public void setFiles(List<PieFile> files) {
        this.files = files;
    }
}
