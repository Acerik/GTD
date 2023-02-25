package model.validation;

import model.FileManager;
import model.files.BasicFile;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValidationManagerTest {

    static String inputDir = ".\\DataInput\\";
    static String cacheDir = ".\\testCache\\";
    static String validatorDir = ".\\DataValidators\\";
    static List<BasicFile> basicFiles = new ArrayList<>();
    static ValidationManager validationManager;

    @BeforeAll
    static void beforeAll() {
        FileManager fileManager = new FileManager(cacheDir, inputDir);
        fileManager.loadFiles();
        basicFiles = fileManager.getInputDataBasicFileList();
        validationManager = new ValidationManager();
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(new File(cacheDir));
    }

    @Test
    @Order(1)
    void setValidatorsDirectory() {
        assertTrue(validationManager.getValidatorsList().isEmpty());
        validationManager.setValidatorsDirectory(validatorDir);
        assertFalse(validationManager.getValidatorsList().isEmpty());
    }

    @Test
    @Order(2)
    void setValidationSchemeForFiles() {
        for(BasicFile bf : basicFiles){
            if(bf.isXml())
                assertNull(bf.getXsdSchemeName());
        }
        validationManager.setValidationSchemeForFiles(basicFiles);
        for(BasicFile bf : basicFiles){
            if(bf.isXml())
                assertNotNull(bf.getXsdSchemeName());
        }
    }

    @Test
    @Order(3)
    void validateFiles() {
        System.out.println(basicFiles.size());
        for(BasicFile bf : basicFiles){
            if(bf.isXml())
                assertEquals(ValidationEndType.NOT_VALIDATED,bf.getValidationEndType());
        }
        assertNotNull(validationManager.validateFiles(basicFiles, false));
        for(BasicFile bf : basicFiles){
            if(bf.isXml())
                assertNotEquals(bf.getValidationEndType(), ValidationEndType.NOT_VALIDATED);
        }
    }

    @Test
    @Order(4)
    void getValidatorsList() {
        assertNotNull(validationManager.getValidatorsList());
    }

    @Test
    @Order(5)
    void addValidatorsFile() {
        File file = new File(validationManager.getValidatorsList().get(0).getPath());
        int sizeBefore = validationManager.getValidatorsList().size();
        assertFalse(validationManager.addValidatorsFile(file, true));
        assertTrue(validationManager.addValidatorsFile(file, false));
        assertEquals(sizeBefore+1, validationManager.getValidatorsList().size());
    }

    @Test
    @Order(6)
    void removeValidatorFromList(){
        int index = validationManager.getValidatorsList().size()-1;
        int sizeBefore = validationManager.getValidatorsList().size();
        validationManager.removeValidatorFromList(index);
        assertEquals(sizeBefore-1, validationManager.getValidatorsList().size());
    }

    @Test
    @Order(7)
    void addValidatorsDirectory(){
        int size= validationManager.getValidatorsList().size();
        validationManager.addValidatorsDirectory(validatorDir, true);
        assertEquals(size, validationManager.getValidatorsList().size());
        validationManager.addValidatorsDirectory(validatorDir, false);
        assertNotEquals(size, validationManager.getValidatorsList().size());
    }

}