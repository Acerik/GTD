package model.files;


import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    static FileHandler fileHandler;
    static String inputDir = "./DataInput/";
    static String cacheDir = "./testCache/";
    static String exportDir = "./testExport/";

    @BeforeAll
    static void beforeAll(){
        fileHandler = new FileHandler(cacheDir, inputDir);
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(new File(cacheDir));
        FileUtils.deleteDirectory(new File(exportDir));
    }

    @Test
    void loadFiles() {
        assertNotNull(fileHandler.loadFiles());
        assertTrue(new File(cacheDir).exists());
    }

    @Test
    void loadFilesWithPath() {
        List<BasicFile> handler = new ArrayList<>(fileHandler.loadFiles());
        List<BasicFile> staticHandler = new ArrayList<>(FileHandler.loadFilesWithPath(cacheDir));
        assertEquals(handler.size(), staticHandler.size());
        for (int i = 0; i < handler.size(); i++) {
            //System.out.print(i + " | ");
            assertEquals(handler.get(i).getPath(), staticHandler.get(i).getPath());
            assertEquals(handler.get(i).getName(), staticHandler.get(i).getName());
            assertEquals(handler.get(i).getXsdSchemeName(), staticHandler.get(i).getXsdSchemeName());
            assertEquals(handler.get(i).getValidationEndType(), staticHandler.get(i).getValidationEndType());
            assertEquals(handler.get(i).isXml(), staticHandler.get(i).isXml());
            assertEquals(handler.get(i).isXsd(), staticHandler.get(i).isXsd());
            assertEquals(handler.get(i).isZipped(), staticHandler.get(i).isZipped());
            assertEquals(handler.get(i).isValidationDone(), staticHandler.get(i).isValidationDone());

        }
    }

    @Test
    void exportData() throws IOException {
        fileHandler.exportData(fileHandler.loadFiles(), exportDir);
        assertTrue(new File(exportDir).exists());
    }
}