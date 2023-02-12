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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

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

        JMenuItem exportWithConfig = new JMenuItem();
        exportWithConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek exportu pomocí configu", true);
                Map config = loadConfig();
                if(fileManager.getInputDataBasicFileList().isEmpty()){
                    ((FilesTabbedPane)tabbedPane).consoleOut("Nejsou načtena data k exportu.", true);
                    return;
                }
                if(config != null && config.containsKey("dataExport")){
                    exportData(config.get("dataExport").toString());
                    ((FilesTabbedPane)tabbedPane).consoleOut("Export pomocí configu dokončen.", true);
                } else {
                    if(config == null)
                        ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při exportu dat pomocí configu", true);
                    else
                        ((FilesTabbedPane)tabbedPane).consoleOut("Config neobsahuje \"exportData\" pro cestu dat k exportu.", true);
                }
            }
        });
        exportWithConfig.setText("Export podle configu");
        functionMenu.add(exportWithConfig);

        JMenuItem export = new JMenuItem();
        export.setAction(new AbstractAction() { // todo opravit chybu exportu se složkami k zazipování
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileManager.getInputDataBasicFileList().isEmpty()){
                    ((FilesTabbedPane)tabbedPane).consoleOut("Nejsou načtena data k exportu.", true);
                    return;
                }
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setDialogTitle("Vyberte složku pro export dat.");
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Složka", "folder"));
                int i = jFileChooser.showDialog(export, "Potvrdit složku");
                if(i == JFileChooser.APPROVE_OPTION){
                    exportData(jFileChooser.getSelectedFile().getPath());
                }
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
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení dat podle configu", true);
                Map config = loadConfig();
                if(config != null && config.containsKey("dataValidators") && config.containsKey("dataInput")) {
                    loadValidators(config.get("dataValidators").toString());
                    loadDataInput(config.get("dataInput").toString());
                    ((FilesTabbedPane)tabbedPane).consoleOut("Data pomocí configu načtena", true);
                } else {
                    if(config == null)
                        ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při načítání dat pomocí configu", true);
                    else if (!config.containsKey("dataValidators"))
                        ((FilesTabbedPane)tabbedPane).consoleOut("Config neobsahuje \"dataValidators\" pro cestu ke složce s validátory.", true);
                    else if (!config.containsKey("dataInput"))
                        ((FilesTabbedPane)tabbedPane).consoleOut("Config neobsahuje \"dataInput\" pro cestu ke složce se vstupními daty.", true);
                }
            }
        });
        loadInputAndValidatorsFromConfig.setText("Načtení vst. dat a validátorů z configu");
        loadSources.add(loadInputAndValidatorsFromConfig);

        JMenuItem loadInputFromConfig = new JMenuItem();
        loadInputFromConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení dat podle configu", true);
                Map config = loadConfig();
                if(config != null && config.containsKey("dataInput")) {
                    loadDataInput(config.get("dataInput").toString());
                    ((FilesTabbedPane)tabbedPane).consoleOut("Data pomocí configu načtena", true);
                } else {
                    if(config == null)
                        ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při načítání dat pomocí configu", true);
                    else if (!config.containsKey("dataInput"))
                        ((FilesTabbedPane)tabbedPane).consoleOut("Config neobsahuje \"dataInput\" pro cestu ke složce se vstupními daty.", true);
                }
            }
        });
        loadInputFromConfig.setText("Načtení vstupních dat z configu");
        loadSources.add(loadInputFromConfig);

        JMenuItem loadValidatorsFromConfig = new JMenuItem();
        loadValidatorsFromConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení dat podle configu", true);
                Map config = loadConfig();
                if(config != null && config.containsKey("dataValidators")) {
                    loadValidators(config.get("dataValidators").toString());
                    ((FilesTabbedPane)tabbedPane).consoleOut("Data pomocí configu načtena", true);
                } else {
                    if(config == null)
                        ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při načítání dat pomocí configu", true);
                    else if (!config.containsKey("dataValidators"))
                        ((FilesTabbedPane)tabbedPane).consoleOut("Config neobsahuje \"dataValidators\" pro cestu ke složce s validátory.", true);
                }
            }
        });
        loadValidatorsFromConfig.setText("Načtení validátorů z configu");
        loadSources.add(loadValidatorsFromConfig);

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

    private void exportData(String dirPath){
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek exportování dat.", true);
        int dialogResult = JOptionPane.YES_OPTION;
        if(new File(dirPath).exists() && Objects.requireNonNull(new File(dirPath).listFiles()).length > 0){
            dialogResult = JOptionPane.showConfirmDialog(null,
                    "Složka \"" + dirPath + "\", již existuje a není prázdná, chcete opravdu exportovat do stejné složky?",
                    "Složka již existuje.",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        if(dialogResult == JOptionPane.YES_OPTION){
            fileManager.exportDataFromCache(dirPath);
            ((FilesTabbedPane)tabbedPane).consoleOut("Exportování dat dokončeno.", true);
        } else {
            ((FilesTabbedPane)tabbedPane).consoleOut("Export přerušen, složka již existuje a obsahuje data.", true);
        }
    }

    private Map loadConfig(){
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení config souboru", true);
        Map config = null;
        File configFile = new File("./gdtconfig.json");
        if(!configFile.exists()) {
            ((FilesTabbedPane)tabbedPane).consoleOut("Config nebyl nalezen ./gdtconfig.json", true);
            int dialogResult = JOptionPane.showConfirmDialog(null,
                    "Chcete nechat vygenerovat výchozí config?",
                    "Config nenalezen.",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(dialogResult == JOptionPane.YES_OPTION){
                config = createConfig();
            }
        } else {
            try {
                String configJson = FileUtils.readFileToString(configFile);
                config = new Gson().fromJson(configJson, Map.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ((FilesTabbedPane)tabbedPane).consoleOut("Čtení configu dokončeno", true);
        return config;
    }

    private Map createConfig(){
        ((FilesTabbedPane)tabbedPane).consoleOut("Vytváření configu.", true);
        Map config = new IdentityHashMap();
        JFileChooser configFileChooser = new JFileChooser();
        configFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        configFileChooser.setFileFilter(new FileNameExtensionFilter("Složka", "folder"));

        configFileChooser.setDialogTitle("Vyberte výchozí složku pro vstupní data.");
        int i = configFileChooser.showDialog(null, "Potvrdit složku");
        if(i == JFileChooser.APPROVE_OPTION){
            config.put("dataInput", configFileChooser.getSelectedFile().getPath());
        }

        configFileChooser.setDialogTitle("Vyberte výchozí složku pro validátory dat.");
        i = configFileChooser.showDialog(null, "Potvrdit složku");
        if(i == JFileChooser.APPROVE_OPTION){
            config.put("dataValidators", configFileChooser.getSelectedFile().getPath());
        }

        configFileChooser.setDialogTitle("Vyberte výchozí složku pro export dat.");
        i = configFileChooser.showDialog(null, "Potvrdit složku");
        if(i == JFileChooser.APPROVE_OPTION){
            config.put("dataExport", configFileChooser.getSelectedFile().getPath());
        }
        String jsonConfig = new Gson().toJson(config);
        try {
            FileUtils.writeStringToFile(new File("./gdtconfig.json"), jsonConfig);
            ((FilesTabbedPane)tabbedPane).consoleOut("Config byl uložen ./gdtconfig.json", true);
        } catch (IOException e) {
            e.printStackTrace();
            ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při ukládání configu.", true);
        }

        return config;
    }
}
