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
    private final FileManager fileManager;
    private ValidationManager validationManager;
    private final JTabbedPane tabbedPane;

    public MainMenuBar(FileManager fileManager, ValidationManager validationManager, JTabbedPane tabbedPane){
        super();
        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.tabbedPane = tabbedPane;

        initLoadSourcesMenu();
        loadSources.setVisible(true);
        this.add(loadSources);
    }

    private void initLoadSourcesMenu() {
        loadSources = new JMenu("Načtení zdrojů");

        JMenuItem loadInputAndValidatorsFromConfig = new JMenuItem();
        loadInputAndValidatorsFromConfig.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start reading config");
                File configFile = new File("./gdtconfig.json");
                if(!configFile.exists()) {
                    System.out.println("Config is not exists");
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
                System.out.println("Config reading is done");
            }
        });
        loadInputAndValidatorsFromConfig.setText("Načtení dat z configu");
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
                    System.out.println(jFileChooser.getSelectedFile());
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
                    System.out.println(jFileChooser.getSelectedFile());
                    loadValidators(jFileChooser.getSelectedFile().getPath());
                }
            }
        });
        loadValidators.setText("Načíst složku s validátory");
        loadSources.add(loadValidators);
    }

    private void loadDataInput(String dirPath){
        System.out.println("Start loading data input");
        fileManager.setInputDirectory(dirPath);
        fileManager.loadFiles();
        try {
            ((FilesTabbedPane)tabbedPane).initInputTree();
            System.out.println("Data input is loaded");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void loadValidators(String dirPath){
        System.out.println("Start loading data validators");
        validationManager = new ValidationManager(dirPath);
        ((FilesTabbedPane)tabbedPane).initValidatorList(validationManager.getValidatorsList());
        System.out.println("Data validators are loaded");
    }
}
