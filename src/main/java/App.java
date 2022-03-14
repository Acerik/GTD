import model.FileManager;
import model.files.BasicFile;

public class App {


    public static void main(String[] args) {
        FileManager fileManager = new FileManager("./Cache/", "./DataInput/");
        fileManager.prepareInputData();

        for(BasicFile file : fileManager.getInputDataBasicFileList()){
            System.out.println(file);
        }

        System.out.println("Exporting");
        fileManager.exportDataFromCache("./Export/");
        System.out.println("Data exported");

    }
}
