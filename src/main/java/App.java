import gui.MainFrame;
import model.FileManager;
import model.files.BasicFile;
import model.validation.ValidationManager;

public class App {


    public static void main(String[] args) {
        FileManager fileManager = new FileManager("./Cache/");
        ValidationManager validationManager = new ValidationManager();
        MainFrame mainFrame = new MainFrame(800,600, fileManager, validationManager);
        mainFrame.setVisible(true);
    }
}
