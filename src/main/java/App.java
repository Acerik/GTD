import gui.MainFrame;
import model.FileManager;
import model.validation.ValidationManager;

public class App {


    public static void main(String[] args) {
        FileManager fileManager = new FileManager("./Cache/");
        ValidationManager validationManager = new ValidationManager();
        MainFrame mainFrame = new MainFrame(800,600, fileManager, validationManager);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
