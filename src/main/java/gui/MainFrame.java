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

    private final JPanel mainPanel;
    private final JMenuBar menuBar;
    private final JTabbedPane jTabbedPane;


    public MainFrame(int width, int height, FileManager fileManager, ValidationManager validationManager){
        super("Generátor testovacích dat");
        setSize(width,height);
        setMinimumSize(new Dimension(width, height));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.fileManager = fileManager;
        this.validationManager = validationManager;
        this.mainPanel = new JPanel(new BorderLayout());
        this.jTabbedPane = new FilesTabbedPane(fileManager, validationManager, this);
        this.menuBar = new MainMenuBar(fileManager, validationManager, jTabbedPane);

        initGui();
        initListeners();

        this.setLayout(null);
        this.setVisible(true);
    }

    private void initGui(){
        menuBar.setVisible(true);
        this.setJMenuBar(menuBar);
        jTabbedPane.setVisible(true);
        this.add(jTabbedPane);

        /*
        mainPanel.add(jTabbedPane, BorderLayout.EAST);
        mainPanel.setVisible(true);
        this.add(mainPanel);

         */
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
