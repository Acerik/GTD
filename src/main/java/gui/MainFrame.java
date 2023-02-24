package gui;

import model.FileManager;
import model.validation.ValidationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Hlavní okno aplikace, obsahuje ostatní komponenty.
 * @see JFrame
 * @author Matěj Váňa
 * */

public class MainFrame extends JFrame {

    private final JPanel editorPanel;
    private final JMenuBar menuBar;
    private final JTabbedPane jTabbedPane;


    public MainFrame(int width, int height, FileManager fileManager, ValidationManager validationManager){
        super("Generátor testovacích dat");
        setSize(width,height);
        setMinimumSize(new Dimension(width, height));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // lepší vzhled
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //Překlad ui dialogů
        UIManager.put("OptionPane.yesButtonText", "Ano");
        UIManager.put("OptionPane.noButtonText", "Ne");
        UIManager.put("OptionPane.cancelButtonText", "Zrušit");
        UIManager.put("FileChooser.cancelButtonText", "Zrušit");

        this.editorPanel = new TextEditor();
        this.jTabbedPane = new FilesTabbedPane(fileManager, validationManager, this, (TextEditor) editorPanel);
        this.menuBar = new MainMenuBar(fileManager, validationManager, jTabbedPane);

        initGui();
        initListeners();

        this.setVisible(true);
    }

    /**
     * Slouží k načtení základního GUI, přidá jednotlivé části okna
     * */
    private void initGui(){
        this.setLayout(new BorderLayout(5,5));
        menuBar.setVisible(true);
        this.setJMenuBar(menuBar);
        jTabbedPane.setVisible(true);
        this.add(jTabbedPane, BorderLayout.WEST);

        editorPanel.setVisible(true);
        this.add(editorPanel, BorderLayout.CENTER);

    }

    /**
     * Přidání listeneru pro resize
     * */
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
