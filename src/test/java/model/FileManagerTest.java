package model;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileManagerTest {

    static String inputDir = ".\\DataInput\\";
    static String cacheDir = ".\\testCache\\";
    static String exportDir = ".\\testExport\\";
    static FileManager fileManager;

    @BeforeAll
    static void beforeAll() {
        fileManager = new FileManager(cacheDir, inputDir);
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(new File(cacheDir));
        FileUtils.deleteDirectory(new File(exportDir));
    }

    @Test
    @Order(1)
    void loadFiles(){
        assertTrue(fileManager.getInputDataBasicFileList().isEmpty());
        fileManager.loadFiles();
        assertFalse(fileManager.getInputDataBasicFileList().isEmpty());
    }

    @Test
    void exportDataFromCache() {
        fileManager.exportDataFromCache(exportDir);
        assertTrue(new File(exportDir).exists());
    }

    @Test
    void getInputDataBasicFileList() {
        assertFalse(fileManager.getInputDataBasicFileList().isEmpty());
    }

    @Test
    void setInputDirectory() {
        fileManager.setInputDirectory("test");
        assertEquals("test",fileManager.getInputDirectory());
        fileManager.setInputDirectory(inputDir);
    }

    @Test
    void setWorkingDirectory() {
        fileManager.setWorkingDirectory("test");
        assertEquals("test", fileManager.getWorkingDirectory());
        fileManager.setWorkingDirectory(cacheDir);
    }

    @Test
    void getWorkingDirectory() {
        assertEquals(cacheDir, fileManager.getWorkingDirectory());
    }

    @Test
    void getInputDirectory() {
        assertEquals(inputDir, fileManager.getInputDirectory());
    }
}