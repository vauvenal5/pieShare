package org.pieShare.pieShareApp.service.fileService;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
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
		pieFile.setSize(file.length());

        if (file.exists()) {
            pieFile.setMd5(hashService.hashStream(file));
        } else {
            pieFile.setDeleted(true);
        }
        return pieFile;
    }

    @Override
    public List<PieFile> getAllFiles() throws IOException {
        List<PieFile> allFiles = new ArrayList<PieFile>();
        allFiles.addAll(getFileList(configuration.getWorkingDir()));

        return allFiles;
    }

    private List<PieFile> getFileList(File parentDir) {
        List<PieFile> tempFileList = new ArrayList<PieFile>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                tempFileList.addAll(getFileList(file));
            } else {
                try {
                    tempFileList.add(getPieFile(file));
                } catch (IOException ex) {
                    PieLogger.error(this.getClass(), "File could not be accessed: {} ", ex);
                }
            }
        }
        return tempFileList;
    }

}
