package gui;

import model.FileManager;
import model.validation.ValidationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainFrame extends JFrame {

    private final FileManager fileManager;
    private final ValidationManager validationManager;

    private final JPanel editorPanel;
    private final JMenuBar menuBar;
    private final JTabbedPane jTabbedPane;


    public MainFrame(int width, int height, FileManager fileManager, ValidationManager validationManager){
        super("Generátor testovacích dat");
        setSize(width,height);
        setMinimumSize(new Dimension(width, height));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.editorPanel = new TextEditor();
        this.jTabbedPane = new FilesTabbedPane(fileManager, validationManager, this, (TextEditor) editorPanel);
        this.menuBar = new MainMenuBar(fileManager, validationManager, jTabbedPane);

        initGui();
        initListeners();

        this.setVisible(true);
    }

    private void initGui(){
        this.setLayout(new BorderLayout(5,5));
        menuBar.setVisible(true);
        this.setJMenuBar(menuBar);
        jTabbedPane.setVisible(true);
        this.add(jTabbedPane, BorderLayout.WEST);

        editorPanel.setVisible(true);
        this.add(editorPanel, BorderLayout.CENTER);

    }

    private void initListeners(){
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //super.componentResized(e);
                ((FilesTabbedPane)jTabbedPane).onResize();

            }
        });
    }

}
