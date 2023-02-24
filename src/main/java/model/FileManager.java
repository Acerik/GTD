package model;

import model.files.BasicFile;
import model.files.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída slouží k obsluze souborů, načítání, export, nastavení cest. Jedná se o jednodušší přístup k {@link FileHandler}
 * @see FileHandler
 * @author Matěj Váňa
 * */
public class FileManager {
    private String workingDirectory;
    private String inputDirectory;
    private FileHandler filehandler;

    private List<BasicFile> inputDataBasicFileList;

    /**
     * Konstruktor
     * @param workingDirectory specifikace složky, kde se bude pracovat se soubory (cache)
     * @param inputDirectory specifikace složky pro vstup dat
     */
    public FileManager(String workingDirectory,String inputDirectory) {
        this.workingDirectory = workingDirectory;
        this.inputDirectory = inputDirectory;
        inputDataBasicFileList = new ArrayList<>();
    }
    /**
     * Konstruktor
     * @param workingDirectory specifikace složky, kde se bude pracovat se soubory (cache)
     */
    public FileManager(String workingDirectory){
        this.workingDirectory = workingDirectory;
        this.inputDataBasicFileList = new ArrayList<>();
    }

    /**
     * Dojde k načtení souborů pomocí zadaných cest, může vypsat chybu do konzole, pokud nejsou nastaveny cesty
     * Pokud jsou nastaveny cesty načte data do paměti.
     * */
    public void loadFiles(){
        if(filehandler != null){
            System.err.println("Již existuje");
        }
        if(inputDirectory != null && workingDirectory != null){
            this.filehandler = new FileHandler(workingDirectory, inputDirectory);
            prepareInputData();
        } else {
            System.err.println("Nejsou zadány složky");
            this.filehandler = null;
        }
    }

    /**
     * Načte cesty k souborům, zjistí, které jsou v zipu a odzipuje je
     */
    private void prepareInputData(){
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

    /**
     * Nastavení cesty pro vstupní data
     * @param inputDirectory cesta ke složce se vstupními daty
     * */
    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    /**
     * Nastavení pracovní složky (cache), kde se budou editovat soubory a pracovat s nimi, při načtení dojde k rozbalení zip souborů
     * @param workingDirectory cesta k pracovní složce (cache)
     * */
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Získání pracovní složky (cache)
     * @return Vrací aktuálně nastavenou pracovní složku (cache)
     * */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Získání cesty ke složce se vstupními daty
     * @return cesta do složky se vstupními daty
     * */
    public String getInputDirectory() {
        return inputDirectory;
    }
}
