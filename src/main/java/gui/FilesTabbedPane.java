package gui;

import model.FileManager;
import model.files.BasicFile;
import model.validation.ValidationManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Třída slouží k zobrazení komponenty se záložky a obsahem jednotlivých zálože (konzole, vstupní data v jtree, validátory v jlist)
 *
 * @see JTabbedPane
 * @see JTree
 * @author Matěj Váňa
 * */
public class FilesTabbedPane extends JTabbedPane {
    private final JPanel inputPanel;
    private final JTree jTreeInput;
    private final JScrollPane scrollPaneInput;

    private final JPanel validatorsPanel;
    private final JScrollPane scrollPaneValidators;
    private final JList<String> jListValidators;
    private final JFrame frame;
    private boolean askToDelete = true;

    private final JPanel consolePanel;
    private final JScrollPane scrollPaneConsole;
    private final TextArea consoleTextArea;

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
        consolePanel = new JPanel();
        consoleTextArea = new TextArea();
        scrollPaneConsole = new JScrollPane();

        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.frame = frame;
        this.textEditor = textEditor;

        this.setBounds(5,5,305,530);
        this.addTab("Vstupní data",inputPanel);
        this.addTab("Validátory",validatorsPanel);
        this.addTab("Konzole", consolePanel);
        initPanes();
        this.setVisible(true);
    }

    /**
     * Inicializace panelů ze záložek tabbetpane, včetně listenerů na eventy
     * */
    private void initPanes(){
        jListValidators.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListValidators.addListSelectionListener(e -> {
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
        });
        jListValidators.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    int dialogResult = -1;
                    if(askToDelete) {
                        String[] options = new String[]{"Ano", "Ano a už se neptat.", "Ne"};
                        dialogResult = JOptionPane.showOptionDialog(null,
                                "Chcete odebrat validátor s názvem: " + jListValidators.getSelectedValue() + "?",
                                "Odebrání validátoru.", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    }
                    if(dialogResult == 0 || dialogResult == 1 || !askToDelete){
                        int selectedIndex = jListValidators.getSelectedIndex();
                        String value = jListValidators.getSelectedValue();
                        validationManager.removeValidatorFromList(selectedIndex);
                        initValidatorList(validationManager.getValidatorsList());
                        if(selectedIndex < validationManager.getValidatorsList().size()){
                            jListValidators.setSelectedIndex(selectedIndex);
                        } else {
                            jListValidators.setSelectedIndex(validationManager.getValidatorsList().size()-1);
                        }
                        consoleOut("Validátor: " + value + " byl odebrán", true);
                        if(dialogResult == 1){
                            askToDelete = false;
                        }
                    }
                }
            }
        });
        scrollPaneValidators.setViewportView(jListValidators);
        jListValidators.setLayoutOrientation(JList.VERTICAL);
        scrollPaneValidators.setPreferredSize(new Dimension(this.getWidth()-5,this.getHeight()-25));
        validatorsPanel.add(scrollPaneValidators);

        jTreeInput.addTreeSelectionListener(e -> {
            StringBuilder path = new StringBuilder(e.getPath().getPath()[0].toString());
            for (int i = 1; i < e.getPath().getPath().length; i++) {
                path.append("\\").append(e.getPath().getPath()[i].toString());
            }
            String name = e.getPath().getPath()[e.getPath().getPath().length-1].toString();
            String finalPath = path.toString();
            Optional<BasicFile> current = fileManager.getInputDataBasicFileList()
                    .stream().filter(basicFile -> Objects.equals(basicFile.getName(), name) && Objects.equals(basicFile.getPath(), finalPath))
                    .findAny();
            if(current.isPresent()) {
                try {
                    textEditor.openFileInEditor(current.get());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        scrollPaneInput.setViewportView(jTreeInput);
        scrollPaneInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        inputPanel.add(scrollPaneInput);

        consoleTextArea.setEditable(false);
        consoleTextArea.setPreferredSize(new Dimension(this.getWidth()-15, this.getHeight()-35));
        scrollPaneConsole.setViewportView(consoleTextArea);
        scrollPaneConsole.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        consolePanel.add(scrollPaneConsole);
    }

    /**
    * Slouží k načtení dat to stromové struktury na GUI, data pro načtení jsou vybrány na základě informací ve fileManager
     * @see JTree
    */
    public void initInputTree() throws InterruptedException {
        //System.out.println("JTree start");
        consoleOut("Načítání stromové struktury vstupních dat.", true);
        File currentDir = new File(fileManager.getWorkingDirectory());
        jTreeInput.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(currentDir)));
        DefaultTreeModel model = (DefaultTreeModel) jTreeInput.getModel();

        // root celého modelu
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        // začátek rekurzivního průchodu celé složky vč. podsložek
        directoryContentScanner(currentDir, root);
        //System.out.println("JTree done");
        consoleOut("Stromová struktura vstupních dat načtena", true);
    }

    private void directoryContentScanner(File dir, DefaultMutableTreeNode root2) {

        DefaultMutableTreeNode newDirTreeNode;

        // získání všech souborů root složky
        File[] files = dir.listFiles();

        assert files != null;
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

                DefaultMutableTreeNode newfile = new DefaultMutableTreeNode(file.getName());

                // vložení souboru do modelu
                model.insertNodeInto(newfile, root2, root2.getChildCount());
                // obnova změn v modelu
                model.reload();

            }

        }
    }

    /**
    * Slouží k načtení validátorů a do listu v GUI
    * @param validatorList List souborů s validátory
     */
    public void initValidatorList(List<BasicFile> validatorList){
        DefaultListModel<String> validatorsModelList = new DefaultListModel<>();
        for(BasicFile basicFile : validatorList){
            File file = new File(basicFile.getPath());
            validatorsModelList.addElement(file.getName());
        }
        jListValidators.setModel(validatorsModelList);
    }

    /**
    * Funkce slouží pro resize komponent
     */
    public void onResize(){
        this.setBounds(5,5,305,frame.getHeight()-70);
        scrollPaneValidators.setPreferredSize(new Dimension(this.getWidth()-5,this.getHeight()-25));
        scrollPaneInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        scrollPaneConsole.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
    }

    /**
     * Slouží k výpisu do konzole, která je přidána v gui.
     * @param text zadaný string se vypíše do konzole v GUI
     * @param addTime na základě boolean se přidá čas před text.
     * */
    public void consoleOut(String text, boolean addTime){
        if(addTime)
            text = "["+ (new SimpleDateFormat("HH:mm:ss").format(new Date())) + "] " + text;
        consoleTextArea.append(text + "\r\n");
    }
}
