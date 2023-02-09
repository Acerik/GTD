package gui;

import com.google.gson.Gson;
import model.FileManager;
import model.validation.ValidationManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainMenuBar extends JMenuBar {

    private JMenu loadSources;
    private JMenu functionMenu;

    private final FileManager fileManager;
    private final ValidationManager validationManager;
    private final JTabbedPane tabbedPane;

    public MainMenuBar(FileManager fileManager, ValidationManager validationManager, JTabbedPane tabbedPane){
        super();
        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.tabbedPane = tabbedPane;

        initLoadSourcesMenu();
        loadSources.setVisible(true);
        this.add(loadSources);
        initFunctionMenu();
        functionMenu.setVisible(true);
        this.add(functionMenu);
    }

    private void initFunctionMenu(){
        functionMenu = new JMenu("Funkce");

        JMenuItem validate = new JMenuItem();
        validate.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Start validation");
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek validace", true);
                validationManager.setValidationSchemeForFiles(fileManager.getInputDataBasicFileList());
                ((FilesTabbedPane)tabbedPane).consoleOut(validationManager.validateFiles(fileManager.getInputDataBasicFileList(), false), true);
                //System.out.println("Validation done");
                ((FilesTabbedPane)tabbedPane).consoleOut("Validace dokončena", true);
            }
        });
        validate.setText("Validovat");
        functionMenu.add(validate);

        JMenuItem export = new JMenuItem(); // todo export výchozí podle configu, nastavení cache souboru v configu, vytvoření default configu, ošetření configu
        export.setAction(new AbstractAction() { // todo opravit chybu exportu se složkami k zazipování
            @Override
            public void actionPerformed(ActionEvent e) {
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek exportování dat.", true);
                fileManager.exportDataFromCache("./Export/");
                ((FilesTabbedPane)tabbedPane).consoleOut("Exportování dat dokončeno.", true);
            }
        });
        export.setText("Export");
        functionMenu.add(export);
    }

    private void initLoadSourcesMenu() {
        loadSources = new JMenu("Načtení zdrojů");

        JMenuItem loadInputAndValidatorsFromConfig = new JMenuItem();
        loadInputAndValidatorsFromConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Start reading config");
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení config souboru", true);
                File configFile = new File("./gdtconfig.json");
                if(!configFile.exists()) {
                    ((FilesTabbedPane)tabbedPane).consoleOut("Config nebyl nalezen ./gdtconfig.json", true);
                    //System.out.println("Config is not exists");
                } else {
                    try {
                        String configJson = FileUtils.readFileToString(configFile);
                        Map config = new Gson().fromJson(configJson, Map.class);
                        loadValidators(config.get("dataValidators").toString());
                        loadDataInput(config.get("dataInput").toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                ((FilesTabbedPane)tabbedPane).consoleOut("Čtení z configu dokončeno", true);
                //System.out.println("Config reading is done");
            }
        });
        loadInputAndValidatorsFromConfig.setText("Načtení dat a validátorů z configu");
        loadSources.add(loadInputAndValidatorsFromConfig);

        JMenuItem loadDataInput = new JMenuItem();
        loadDataInput.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setDialogTitle("Vyberte složku se vstupními daty.");
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Složka", "folder"));
                int i = jFileChooser.showDialog(loadSources, "Potvrdit složku");
                if(i == JFileChooser.APPROVE_OPTION){
                    //System.out.println(jFileChooser.getSelectedFile());
                    loadDataInput(jFileChooser.getSelectedFile().getPath());
                }
            }
        });
        loadDataInput.setText("Načíst složku se vstupními daty");
        loadSources.add(loadDataInput);

        JMenuItem loadValidators = new JMenuItem();
        loadValidators.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setDialogTitle("Vyberte složku s validátory.");
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Složka", "folder"));
                int i = jFileChooser.showDialog(loadSources, "Potvrdit složku");
                if(i == JFileChooser.APPROVE_OPTION){
                    //System.out.println(jFileChooser.getSelectedFile());
                    loadValidators(jFileChooser.getSelectedFile().getPath());
                }
            }
        });
        loadValidators.setText("Načíst složku s validátory");
        loadSources.add(loadValidators);
    }

    private void loadDataInput(String dirPath){
        //System.out.println("Start loading data input");
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek načítání vstupních dat z: " + dirPath, true);
        fileManager.setInputDirectory(dirPath);
        fileManager.loadFiles();
        try {
            ((FilesTabbedPane)tabbedPane).initInputTree();
            //System.out.println("Data input is loaded");
            ((FilesTabbedPane)tabbedPane).consoleOut("Načítání vstupních dat dokončeno.", true);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void loadValidators(String dirPath){
        //System.out.println("Start loading data validators");
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek načítání validátorů z: " + dirPath, true);
        validationManager.setValidatorsDirectory(dirPath);
        ((FilesTabbedPane)tabbedPane).initValidatorList(validationManager.getValidatorsList());
        //System.out.println("Data validators are loaded");
        ((FilesTabbedPane)tabbedPane).consoleOut("Načítání validátorů dokončeno.", true);
    }
}
