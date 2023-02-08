package gui;

import model.FileManager;
import model.files.BasicFile;
import model.validation.ValidationManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FilesTabbedPane extends JTabbedPane {
    private final JPanel inputPanel;
    private final JTree jTreeInput;
    private final JScrollPane scrollPaneInput;

    private final JPanel validatorsPanel;
    private final JScrollPane scrollPaneValidators;
    private final JList<String> jListValidators;
    private final JFrame frame;

    private final FileManager fileManager;
    private final ValidationManager validationManager;
    private final TextEditor textEditor;

    public FilesTabbedPane(FileManager fileManager, ValidationManager validationManager, JFrame frame, TextEditor textEditor){
        validatorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        jListValidators = new JList<>();
        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        jTreeInput = new JTree(new DefaultMutableTreeNode());
        scrollPaneInput = new JScrollPane();
        scrollPaneValidators = new JScrollPane();
        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.frame = frame;
        this.textEditor = textEditor;

        this.setBounds(5,5,305,530);
        this.addTab("Vstupní data",inputPanel);
        this.addTab("Validátory",validatorsPanel);
        initPanes();
        this.setVisible(true);
    }

    private void initPanes(){
        jListValidators.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListValidators.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Optional<BasicFile> current = validationManager.getValidatorsList().stream().filter(basicFile -> Objects.equals(basicFile.getName(), jListValidators.getSelectedValue())).findAny();
                if(current.isPresent()) {
                    try {
                        textEditor.openFileInEditor(current.get());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                    System.out.println("This validator is not loaded");
            }
        });
        scrollPaneValidators.setViewportView(jListValidators);
        jListValidators.setLayoutOrientation(JList.VERTICAL);
        scrollPaneValidators.setPreferredSize(new Dimension(this.getWidth()-5,this.getHeight()-25));
        validatorsPanel.add(scrollPaneValidators);

        jTreeInput.addTreeSelectionListener(e -> System.out.println(e.getPath()));
        scrollPaneInput.setViewportView(jTreeInput);
        scrollPaneInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        inputPanel.add(scrollPaneInput);
    }


    public void initInputTree() throws InterruptedException {
        System.out.println("JTree start");
        File currentDir = new File(fileManager.getWorkingDirectory());
        jTreeInput.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(currentDir)));
        DefaultTreeModel model = (DefaultTreeModel) jTreeInput.getModel();

        // root celého modelu
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        // začátek rekurzivního průchodu celé složky vč. podsložek
        directoryContentScanner(currentDir, root);
        System.out.println("JTree done");
    }

    private void directoryContentScanner(File dir, DefaultMutableTreeNode root2) {

        DefaultMutableTreeNode newDirTreeNode;

        // získání všech souborů root složky
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file == null) {
                System.out.println("NUll directory found ");
                continue;
            }
            if (file.isDirectory()) {
                // nalezení složky

                if (file.listFiles() == null) {
                    // přeskočení prázdných složek
                    continue;
                }
                // získání modelu
                DefaultTreeModel model = (DefaultTreeModel) jTreeInput.getModel();
                // získání roota modelu
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                // vytvoření node pro novou složku v modelu
                newDirTreeNode = new DefaultMutableTreeNode(file.getName());

                // přidání nové složky do modelu
                root2.add(newDirTreeNode);
                // obnova modelu
                model.reload();

                // rekurzivní průchod nově našlé složky
                directoryContentScanner(file, newDirTreeNode);
            } else {
                // jedná se o soubor

                // získání modelu
                DefaultTreeModel model = (DefaultTreeModel) jTreeInput.getModel();
                // nadřazená složka (root) pro aktuální soubor
                DefaultMutableTreeNode rootForCurrentFile = root2;
                DefaultMutableTreeNode newfile = new DefaultMutableTreeNode(file.getName());

                // vložení souboru do modelu
                model.insertNodeInto(newfile, rootForCurrentFile, rootForCurrentFile.getChildCount());
                // obnova změn v modelu
                model.reload();

            }

        }
    }

    public void initValidatorList(List<BasicFile> validatorList){
        DefaultListModel<String> validatorsModelList = new DefaultListModel<>();
        for(BasicFile basicFile : validatorList){
            File file = new File(basicFile.getPath());
            validatorsModelList.addElement(file.getName());
        }
        jListValidators.setModel(validatorsModelList);
    }

    public void onResize(){
        this.setBounds(5,5,305,frame.getHeight()-70);
        scrollPaneValidators.setPreferredSize(new Dimension(this.getWidth()-5,this.getHeight()-25));
        scrollPaneInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
    }

}
