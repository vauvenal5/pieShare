package org.pieShare.pieShareApp.service.fileService;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileService.api.IFilderIterationCallback;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 * @author richy
 */
public class LocalFileService extends FileServiceBase {

    private IHashService hashService;
    private Provider<PieFile> pieFileProvider;

    public void setHashService(IHashService hashService) {
        this.hashService = hashService;
    }

    public void setPieFileProvider(Provider<PieFile> pieFileProvider) {
        this.pieFileProvider = pieFileProvider;
    }

    @Override
    public PieFile getPieFile(File file) throws IOException {
        PieFile pieFile = this.pieFileProvider.get();

        pieFile.setRelativePath(relativizeFilePath(file));

        pieFile.setName(file.getName());
        pieFile.setLastModified(file.lastModified());

        if (file.exists()) {
            pieFile.setMd5(hashService.hashStream(file));
        } else {
            pieFile.setDeleted(true);
        }
        return pieFile;
    }

//    @Override
//    public List<PieFile> getAllFiles() throws IOException {
//        final List<PieFile> allFiles = new ArrayList<PieFile>();
//		
//		this.walkFilderTree(configuration.getWorkingDir(), new IFilderIterationCallback() {
//			@Override
//			public void handleFile(PieFile file) {
//				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//			}
//
//			@Override
//			public void handleFolder(PieFolder folder) {
//				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//			}
//		});
//
//        return allFiles;
//    }

    

}
