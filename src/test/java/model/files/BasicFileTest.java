package model.files;

import model.files.BasicFile;
import model.validation.ValidationEndType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class BasicFileTest {

    String pathFile = ".\\DataInput\\1_IDTDG\\F604_17XTSO-CS------W-to-10V1001C--00264T.xml";
    BasicFile basicFile;

    @BeforeEach
    void setUp(){
        basicFile = new BasicFile(new File(pathFile));
    }

    @Test
    void getPath() {
        assertEquals(pathFile, basicFile.getPath());
    }

    @Test
    void setPath() {
        String newPath = "test";
        basicFile.setPath(newPath);
        assertEquals(newPath, basicFile.getPath());
    }

    @Test
    void getName() {
        String name = "F604_17XTSO-CS------W-to-10V1001C--00264T.xml";
        assertEquals(name, basicFile.getName());
    }

    @Test
    void setName() {
        basicFile.setName("name");
        assertEquals("name", basicFile.getName());
    }

    @Test
    void isZipped() {
        assertFalse(basicFile.isZipped());
        basicFile = new BasicFile(new File(".\\DataInput\\1_IDTDG\\F605_17XTSO-CS------W-to-10V1001C--00264T.zip"));
        assertTrue(basicFile.isZipped());
    }

    @Test
    void isXml() {
        assertTrue(basicFile.isXml());
        basicFile = new BasicFile(new File(".\\DataInput\\1_IDTDG\\F605_17XTSO-CS------W-to-10V1001C--00264T.zip"));
        assertFalse(basicFile.isXml());
    }

    @Test
    void isXsd() {
        assertFalse(basicFile.isXsd());
        basicFile = new BasicFile(new File(".\\DataValidators\\capacity-3.0.xsd"));
        assertTrue(basicFile.isXsd());
    }

    @Test
    void getXsdSchemeName() {
        basicFile.setXsdSchemeName("xsd-scheme");
        assertEquals("xsd-scheme", basicFile.getXsdSchemeName());
    }

    @Test
    void setXsdSchemeName() {
        basicFile.setXsdSchemeName("xsd-scheme");
        assertEquals("xsd-scheme", basicFile.getXsdSchemeName());
    }

    @Test
    void isValidationDone() {
        assertFalse(basicFile.isValidationDone());
    }

    @Test
    void setValidationDone() {
        basicFile.setValidationDone(true);
        assertTrue(basicFile.isValidationDone());
    }

    @Test
    void getValidationEndType() {
        assertEquals(ValidationEndType.NOT_VALIDATED, basicFile.getValidationEndType());
    }

    @Test
    void setValidationEndType() {
        basicFile.setValidationEndType(ValidationEndType.SUCCESS);
        assertEquals(ValidationEndType.SUCCESS, basicFile.getValidationEndType());
    }

    @Test
    void testToString() {
        assertNotNull(basicFile.toString());
    }
}