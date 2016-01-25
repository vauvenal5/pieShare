package org.pieShare.pieShareApp.service.fileService;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.inject.Provider;
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
		pieFile.setSize(file.length());

        if (file.exists()) {
            pieFile.setMd5(hashService.hashStream(file));
        } else {
            pieFile.setDeleted(true);
			pieFile.setLastModified(new Date().getTime());
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
