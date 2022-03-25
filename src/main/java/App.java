import model.FileManager;
import model.files.BasicFile;
import model.validation.ValidationManager;

public class App {


    public static void main(String[] args) {
        FileManager fileManager = new FileManager("./Cache/", "./DataInput/");
        fileManager.prepareInputData();
        ValidationManager validationManager = new ValidationManager("./DataValidators/");
        validationManager.setValidationSchemeForFiles(fileManager.getInputDataBasicFileList());
        for(BasicFile file : fileManager.getInputDataBasicFileList()){
            System.out.println(file);
        }
        validationManager.validateFiles(fileManager.getInputDataBasicFileList());

        /*
        System.out.println("Exporting");
        fileManager.exportDataFromCache("./Export/");
        System.out.println("Data exported");
         */

    }
}
