package gui;

import model.files.BasicFile;
import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TextEditor extends JPanel {

    private final RSyntaxTextArea textArea;

    private final JPanel topPanel;
    private final JLabel fileNameLabel;
    private final JButton saveButton;

    private BasicFile currentOpenBasicFile = null;

    public TextEditor(){
        this.setLayout(new BorderLayout());
        textArea = new RSyntaxTextArea();
        fileNameLabel = new JLabel();
        saveButton = new JButton();
        topPanel = new JPanel(new BorderLayout());
        initGui();
    }

    private void initGui(){
        //text editor
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setViewportView(textArea);
        this.add(sp, BorderLayout.CENTER);

        // top panel
        fileNameLabel.setText("<html>Není otevřen žádný soubor<br>.</html>");
        topPanel.add(fileNameLabel, BorderLayout.WEST);
        saveButton.setText("Uložit soubor");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            try {
                FileUtils.writeStringToFile(new File(currentOpenBasicFile.getPath()), textArea.getText(), false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        topPanel.add(saveButton, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);
    }

    public void openFileInEditor(BasicFile basicFile) throws IOException {
        if(currentOpenBasicFile == null)
            saveButton.setEnabled(true);
        if(currentOpenBasicFile != null){
            // kontrola změny souboru
            if(!Objects.equals(textArea.getText(), FileUtils.readFileToString(new File(currentOpenBasicFile.getPath())))){
                // zeptat se na uložení
                int dialogResult = JOptionPane.showConfirmDialog(null, "Chcete uložit aktuální soubor před otevřením nového souboru?","Uložit?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(dialogResult == JOptionPane.YES_OPTION){
                    FileUtils.writeStringToFile(new File(currentOpenBasicFile.getPath()), textArea.getText(), false);
                }
            }
        }
        textArea.setText(FileUtils.readFileToString(new File(basicFile.getPath())));
        textArea.setCaretPosition(0);
        fileNameLabel.setText("<html>Soubor: " + basicFile.getName() + "<br>" + basicFile.getPath()+"</html>");
        currentOpenBasicFile = basicFile;
    }

}
