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
import java.util.*;

/**
 * Komponenta pro menu v GUI, obsahuje načítání, práci s configem, validace, export
 * @see JMenuBar
 * @author Matěj Váňa
 * */

public class MainMenuBar extends JMenuBar {

    private JMenu loadSources;
    private JMenu functionMenu;
    private JMenu configMenu;

    private final FileManager fileManager;
    private final ValidationManager validationManager;
    private final JTabbedPane tabbedPane;

    private static final String CONFIG_PATH = "./gdtconfig.json";

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

        initConfigMenu();
        configMenu.setVisible(true);
        this.add(configMenu);
    }

    /**
     * Slouží k vytvoření itemů v záložce config, včetně listenerů, vzhledu, umístění
     * */
    private void initConfigMenu(){
        configMenu = new JMenu("Config");

        JMenuItem showConfig = new JMenuItem();
        showConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String, String> config = loadConfig();
                JOptionPane.showMessageDialog(null,
                        String.format("Umístění configu: %s\r\n\r\nCesta pro vstupní data: %s \r\nCesta pro validátory: %s\r\nCesta pro export: %s\r\n",
                                CONFIG_PATH,
                                config.getOrDefault("dataInput", "Není nastaveno"),
                                config.getOrDefault("dataValidators", "Není nastaveno"),
                                config.getOrDefault("dataExport", "Není nastaveno")),
                        "Aktuální config: ",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        showConfig.setText("Zobrazit config");
        showConfig.setIcon(UIManager.getIcon("Menu.arrowIcon"));
        configMenu.add(showConfig);

        JMenuItem createConfigWithDialog = new JMenuItem();
        createConfigWithDialog.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File configFile = new File(CONFIG_PATH);
                if(configFile.exists()){
                    int dialogResult = JOptionPane.showConfirmDialog(null,
                            "Config již existuje, chcete ho nahradit novým pomocí dialogů?",
                            "Config již existuje",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if(dialogResult == JOptionPane.YES_OPTION){
                        createConfig();
                    }
                }
            }
        });
        createConfigWithDialog.setText("Vytvoření configu");
        createConfigWithDialog.setIcon(UIManager.getIcon("FileView.fileIcon"));
        configMenu.add(createConfigWithDialog);

        JMenuItem changeDataInput = new JMenuItem();
        changeDataInput.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map config = loadConfig();
                config.remove("dataInput");
                config.put("dataInput", showDialogFolderChooser("Vyberte cestu ke vstupním datům pro uložení do configu."));
                saveConfig(config);
            }
        });
        changeDataInput.setText("Změnit cestu ke vstupním datům v configu");
        changeDataInput.setIcon(UIManager.getIcon("FileView.computerIcon"));
        configMenu.add(changeDataInput);

        JMenuItem changeValidators = new JMenuItem();
        changeValidators.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map config = loadConfig();
                config.remove("dataValidators");
                config.put("dataValidators", showDialogFolderChooser("Vyberte cestu k validátorům pro uložení do configu."));
                saveConfig(config);
            }
        });
        changeValidators.setText("Změnit cestu k validátorům v configu");
        changeValidators.setIcon(UIManager.getIcon("FileView.computerIcon"));
        configMenu.add(changeValidators);

        JMenuItem changeExport = new JMenuItem();
        changeExport.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map config = loadConfig();
                config.remove("dataExport");
                config.put("dataExport", showDialogFolderChooser("Vyberte cestu pro exportu, pro uložení do configu."));
                saveConfig(config);
            }
        });
        changeExport.setText("Změnit cestu exportu v configu");
        changeExport.setIcon(UIManager.getIcon("FileView.computerIcon"));
        configMenu.add(changeExport);
    }

    /**
     * Slouží k vytvoření itemů v záložce funkce, včetně listenerů, vzhledu, umístění
     * */
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
        validate.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
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
        exportWithConfig.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
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
        export.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        functionMenu.add(export);
    }

    /**
     * Slouží k vytvoření načtení v záložce config, včetně listenerů, vzhledu, umístění
     * */
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
        loadInputAndValidatorsFromConfig.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
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
        loadInputFromConfig.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
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
        loadValidatorsFromConfig.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
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
        loadDataInput.setIcon(UIManager.getIcon("Tree.openIcon"));
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
                    loadValidators(jFileChooser.getSelectedFile().getPath());
                }
            }
        });
        loadValidators.setText("Načíst složku s validátory");
        loadValidators.setIcon(UIManager.getIcon("Tree.openIcon"));
        loadSources.add(loadValidators);
    }

    /**
     * Slouží k načtení vstupní dat ze zadané cesty v parametrech
     * @param dirPath cesta ke složce se vstupními daty
     * */
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

    /**
     * Slouží k načtení validátorů ze zadané cesty v parametrech
     * @param dirPath cesta ke složce s validátory
     * */
    private void loadValidators(String dirPath){
        //System.out.println("Start loading data validators");
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek načítání validátorů z: " + dirPath, true);
        validationManager.setValidatorsDirectory(dirPath);
        ((FilesTabbedPane)tabbedPane).initValidatorList(validationManager.getValidatorsList());
        //System.out.println("Data validators are loaded");
        ((FilesTabbedPane)tabbedPane).consoleOut("Načítání validátorů dokončeno.", true);
    }

    /**
     * Slouží k exportu dat podle cesty zadané v parametrech
     * @param dirPath cesta ke složce s exportem
     * */
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

    /**
     * Načte config do mapy, kterou vrací
     * @return Map vrací mapu s configem
     * */
    private Map loadConfig(){
        ((FilesTabbedPane)tabbedPane).consoleOut("Začátek čtení config souboru", true);
        Map config = null;
        File configFile = new File(CONFIG_PATH);
        if(!configFile.exists()) {
            ((FilesTabbedPane)tabbedPane).consoleOut("Config nebyl nalezen " + CONFIG_PATH, true);
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

    /**
     * Umožňuje skrze dialogová okna vytvořit config, který rovnou vrací.
     * @return Map aktuálně vytvořneý config
     * */
    private Map<String, String> createConfig(){
        ((FilesTabbedPane)tabbedPane).consoleOut("Vytváření configu.", true);
        Map config = new IdentityHashMap();

        String folderPath = showDialogFolderChooser("Vyberte výchozí složku pro vstupní data.");
        if(folderPath != null)
            config.put("dataInput", folderPath);

        folderPath = showDialogFolderChooser("Vyberte výchozí složku pro validátory dat.");
        if(folderPath != null)
            config.put("dataValidators", folderPath);

        folderPath = showDialogFolderChooser("Vyberte výchozí složku pro export dat.");
        if(folderPath != null)
            config.put("dataExport", folderPath);

        saveConfig(config);
        return config;
    }

    /**
     * Slouží k zobrazení okna pro výběr složky/souboru
     * @param title Titulek dialogu
     * @return String s cestou k vybrané složce, případně null pokud není vybrána
     * */
    private String showDialogFolderChooser(String title){
        JFileChooser configFileChooser = new JFileChooser();
        configFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        configFileChooser.setFileFilter(new FileNameExtensionFilter("Složka", "folder"));

        configFileChooser.setDialogTitle(title);
        int i = configFileChooser.showDialog(null, "Potvrdit složku");
        if(i == JFileChooser.APPROVE_OPTION){
            return configFileChooser.getSelectedFile().getPath();
        }
        return null;
    }

    /**
     * Uložení configu
     * @param config Mapa s configem k uložení.
     * */
    private void saveConfig(Map config){
        String jsonConfig = new Gson().toJson(config);
        try {
            FileUtils.writeStringToFile(new File(CONFIG_PATH), jsonConfig, false);
            ((FilesTabbedPane)tabbedPane).consoleOut("Config byl uložen " + CONFIG_PATH, true);
        } catch (IOException e) {
            e.printStackTrace();
            ((FilesTabbedPane)tabbedPane).consoleOut("Chyba při ukládání configu.", true);
        }
    }
}
