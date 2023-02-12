package model;

import model.files.BasicFile;
import model.files.FileHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
    private String workingDirectory;
    private String inputDirectory;
    private FileHandler filehandler;

    private List<BasicFile> inputDataBasicFileList;

    /**
     *
     * @param workingDirectory specifikace složky, kde se bude pracovat se soubory (cache)
     * @param inputDirectory specifikace složky pro vstup dat
     */
    public FileManager(String workingDirectory,String inputDirectory) {
        this.workingDirectory = workingDirectory;
        this.inputDirectory = inputDirectory;
        inputDataBasicFileList = new ArrayList<>();
    }

    public FileManager(String workingDirectory){
        this.workingDirectory = workingDirectory;
        this.inputDataBasicFileList = new ArrayList<>();
    }

    public FileManager(){
        this.inputDataBasicFileList = new ArrayList<>();
    }

    public void loadFiles(){
        if(filehandler != null){
            System.out.println("Již existuje");
        }
        if(inputDirectory != null && workingDirectory != null){
            this.filehandler = new FileHandler(workingDirectory, inputDirectory);
            prepareInputData();
        } else {
            System.out.println("Nejsou zadány složky");
            this.filehandler = null;
        }
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
        try {
            filehandler.exportData(inputDataBasicFileList, exportDirectory);
            //FileUtils.deleteDirectory(cacheFile);
        }catch (IOException e) {
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

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getInputDirectory() {
        return inputDirectory;
    }
}
