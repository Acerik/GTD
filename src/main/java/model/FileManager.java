package model;

import model.files.BasicFile;
import model.files.FileHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final String workingDirectory;
    private final FileHandler filehandler;

    private List<BasicFile> inputDataBasicFileList;

    /**
     *
     * @param workingDirectory specifikace složky, kde se bude pracovat se soubory (cache)
     * @param inputDirectory specifikace složky pro vstup dat
     */
    public FileManager(String workingDirectory,String inputDirectory) {
        this.workingDirectory = workingDirectory;
        this.filehandler = new FileHandler(workingDirectory, inputDirectory);
        inputDataBasicFileList = new ArrayList<>();
    }

    /**
     * Načte cesty k souborům, zjistí, které jsou v zipu a odzipuje je
     */
    public void prepareInputData(){
        inputDataBasicFileList = filehandler.loadFiles();
    }

    /**
     * Slouží k exportu dat do konkrétní složky z cache
     * @param exportDirectory specifikace složky, kam se mají exportovat data
     */
    public void exportDataFromCache(String exportDirectory){
        filehandler.exportData(inputDataBasicFileList);
        File cacheFile = new File(workingDirectory);
        try {
            FileUtils.copyDirectory(cacheFile, new File(exportDirectory));
            FileUtils.deleteDirectory(cacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return vrací list BasicFile
     */
    public List<BasicFile> getInputDataBasicFileList() {
        return inputDataBasicFileList;
    }
}
