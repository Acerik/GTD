package gui;

import model.FileManager;
import model.validation.ValidationManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;

public class MainMenuBar extends JMenuBar {

    private JMenu loadSources;
    private JMenuItem loadValidators;
    private JMenuItem loadDataInput;
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

        loadDataInput = new JMenuItem();
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
                    fileManager.setInputDirectory(jFileChooser.getSelectedFile().getPath());
                    fileManager.loadFiles();
                    ((FilesTabbedPane)tabbedPane).initInputTree(fileManager.getInputDataBasicFileList());
                }
            }
        });
        loadDataInput.setText("Načíst složku se vstupními daty");
        loadSources.add(loadDataInput);

        loadValidators = new JMenuItem();
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
                    validationManager = new ValidationManager(jFileChooser.getSelectedFile().getPath());
                    ((FilesTabbedPane)tabbedPane).initValidatorList(validationManager.getValidatorsList());
                }
            }
        });
        loadValidators.setText("Načíst složku s validátory");
        loadSources.add(loadValidators);
    }
}
