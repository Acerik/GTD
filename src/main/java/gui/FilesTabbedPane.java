package gui;

import model.FileManager;
import model.files.BasicFile;
import model.validation.ValidationManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FilesTabbedPane extends JTabbedPane {
    private final JPanel inputPanel;
    private JTree jTreeInput;
    private final DefaultTreeModel treeModelInput;
    private final JScrollPane scrollPaneInput;

    private final JPanel validatorsPanel;
    private final JScrollPane scrollPaneValidators;
    private final JList<String> jListValidators;
    private final JFrame frame;

    private final FileManager fileManager;
    private final ValidationManager validationManager;

    public FilesTabbedPane(FileManager fileManager, ValidationManager validationManager, JFrame frame){
        validatorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        jListValidators = new JList<String>();
        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        treeModelInput = new DefaultTreeModel(new DefaultMutableTreeNode());
        jTreeInput = new JTree(treeModelInput);
        scrollPaneInput = new JScrollPane();
        scrollPaneValidators = new JScrollPane();
        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.frame = frame;

        this.setBounds(5,5,305,530);
        this.addTab("Vstupní data",inputPanel);
        this.addTab("Validátory",validatorsPanel);
        testTree(50);
        initPanes();
        this.setVisible(true);
    }

    private void initPanes(){
        jListValidators.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListValidators.addListSelectionListener(e -> System.out.println(e.getFirstIndex()));
        scrollPaneValidators.setViewportView(jListValidators);
        jListValidators.setLayoutOrientation(JList.VERTICAL);
        scrollPaneValidators.setPreferredSize(new Dimension(this.getWidth()-5,this.getHeight()-25));
        validatorsPanel.add(scrollPaneValidators);

        jTreeInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        scrollPaneInput.setViewportView(jTreeInput);
        scrollPaneInput.add(jTreeInput);
        scrollPaneInput.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        scrollPaneInput.setBackground(Color.BLUE);
        inputPanel.setPreferredSize(new Dimension(this.getWidth()-5, this.getHeight()-25));
        inputPanel.setBackground(Color.red);
        inputPanel.add(scrollPaneInput);
        inputPanel.setVisible(true);
    }

    public void testTree(int num){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        for (int i = 0; i < num; i++) {
            root.insert(new DefaultMutableTreeNode("test " + i), i);
        }
        treeModelInput.setRoot(root);
        treeModelInput.reload();
        jTreeInput.setModel(treeModelInput);
    }

    public void initInputTree(List<BasicFile> inputList){
        System.out.println(inputList.size());
        List<String> directories = new ArrayList<>();
        for(BasicFile file : inputList){
            String[] pathParts = file.getPath().split(Pattern.quote("\\"));
            for (int i = 0; i < pathParts.length-1; i++) {
                if(pathParts[i].length() < 2)
                    continue;
                if(pathParts.length - 2 == i) {
                    String nameWithPath = "";
                    for (int x = i; x > 0; x--) {
                        nameWithPath = x + ";" + pathParts[x] + ";" + nameWithPath;
                    }
                    if(nameWithPath.length() > 0)
                        nameWithPath = nameWithPath.substring(0, nameWithPath.length()-1);
                    if(!directories.contains(nameWithPath)){
                        directories.add(nameWithPath);
                    }
                }
            }
        }
        System.out.println(inputList.get(3));
        directories.forEach(System.out::println);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directories.get(0).split(";")[1]);
        root.insert(new DefaultMutableTreeNode(directories.get(0).split(";")[3]), 0);
        System.out.println(root.getChildAt(0));
        System.out.println(Arrays.toString(root.getPath()));
        List<DefaultMutableTreeNode> dirs = new ArrayList<>();

        treeModelInput.setRoot(root);
        treeModelInput.reload();
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
    }

}
