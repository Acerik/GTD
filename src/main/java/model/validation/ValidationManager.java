package model.validation;

import model.files.BasicFile;
import model.files.FileHandler;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ValidationManager {

    private final String validatorsDirectory;

    public List<BasicFile> validatorsList;

    public ValidationManager(String validatorsDirectory){
        this.validatorsDirectory = validatorsDirectory;
        loadValidators();
    }

    private void loadValidators(){
        validatorsList = FileHandler.loadFilesWithPath(validatorsDirectory);
    }

    public void setValidationSchemeForFiles(List<BasicFile> basicFiles) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        basicFiles.forEach(basicFile -> {
            if(basicFile.isXml()) {
                try {
                    SAXParser saxParser = factory.newSAXParser();
                    InfoForValidationHandlerSax handlerSax = new InfoForValidationHandlerSax();
                    saxParser.parse(basicFile.getPath(), handlerSax);
                    basicFile.setXsdSchemeName(handlerSax.getValidationXsdName());
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void validateFiles(List<BasicFile> basicFiles){
        AtomicInteger filesValidated = new AtomicInteger();
        AtomicInteger filesValidatedSuccess = new AtomicInteger();
        AtomicInteger filesValidatedErrorValidation = new AtomicInteger();
        AtomicInteger filesValidatedErrorNoXsd = new AtomicInteger();
        AtomicInteger filesValidatedErrorNoXsdScheme = new AtomicInteger();
        List<String> missingXsdFilesNames = new ArrayList<>();
        List<String> missingXsdSchemeFilesPath = new ArrayList<>();

        basicFiles.stream().filter(basicFile -> basicFile.isXml()).forEach(basicFile -> {
            filesValidated.getAndIncrement();
            if(basicFile.getXsdSchemeName() != null){
                if(validatorsList.stream().anyMatch(validator -> validator.isXsd() && validator.getName().equals(basicFile.getXsdSchemeName()))){
                    String validatorPath = validatorsList.stream().filter(validator-> validator.isXsd() && validator.getName().equals(basicFile.getXsdSchemeName())).findFirst().get().getPath();
                    Source xmlFile = new StreamSource(new File(basicFile.getPath()));
                    try{
                        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        Schema schema = schemaFactory.newSchema(new File(validatorPath));
                        Validator validator = schema.newValidator();
                        validator.validate(xmlFile);
                        basicFile.setValidationEndType(ValidationEndType.SUCCESS);
                        filesValidatedSuccess.getAndIncrement();
                    } catch (SAXException e) {
                        e.printStackTrace();
                        //is not valid
                        basicFile.setValidationEndType(ValidationEndType.VALIDATION_ERROR);
                        filesValidatedErrorValidation.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // there is not xsd file for validation
                    filesValidatedErrorNoXsd.getAndIncrement();
                    System.out.println("There is no xsd file for validation: " + basicFile.getXsdSchemeName() + " FilePath " + basicFile.getPath());
                    if(!missingXsdFilesNames.contains(basicFile.getXsdSchemeName())){
                        missingXsdFilesNames.add(basicFile.getXsdSchemeName());
                    }
                    basicFile.setValidationEndType(ValidationEndType.MISSING_XSD_FILE);
                }
            } else {
                filesValidatedErrorNoXsdScheme.getAndIncrement();
                //System.out.println("There is no scheme for file: " + basicFile);
                if(!missingXsdSchemeFilesPath.contains(basicFile.getPath())){
                    missingXsdSchemeFilesPath.add(basicFile.getPath());
                }
                basicFile.setValidationEndType(ValidationEndType.MISSING_XSD_SCHEME);
            }
            basicFile.setValidationDone(true);
        });
        System.out.println("Validated files : " + filesValidated);
        System.out.println("Validated files Success : " + filesValidatedSuccess);
        System.out.println("Validated files error validation : " + filesValidatedErrorValidation);
        System.out.println("Validated files error no xsd file : " + filesValidatedErrorNoXsd);
        System.out.println("Validated files error no xsd scheme : " + filesValidatedErrorNoXsdScheme);
        System.out.println("Missing xsd files [" + missingXsdFilesNames.size() + "] " + missingXsdFilesNames);
        System.out.println("File paths that missing xsd scheme [" + missingXsdSchemeFilesPath.size() + "] " + missingXsdSchemeFilesPath);
    }
}
