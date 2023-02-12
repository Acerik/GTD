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

    private String validatorsDirectory;

    private List<BasicFile> validatorsList;

    public ValidationManager(){

    }

    public ValidationManager(String validatorsDirectory){
        setValidatorsDirectory(validatorsDirectory);
    }

    private void loadValidators(){
        validatorsList = FileHandler.loadFilesWithPath(validatorsDirectory);
    }

    /**
     * Nachystá xsd schémata pro vstupní data, aby mohlo dojít k jejich validaci
     * @param basicFiles List základních souborů dat, ve kterých je potřeba najít xsd schémata
     * @return String s možnými chybami, případně výstupem s informacemi vhodnými do konzole.
     */
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

    /**
     * Slouží k validaci xml souborů
     * @param basicFiles List základních souborů, které se mají validatovat na základě předem nalezlých xsd schémat.
     * @return String s možnými chybami, případně výstupem s informacemi vhodnými do konzole.
     */
    public String validateFiles(List<BasicFile> basicFiles, boolean findXsdSchemes){
        if(findXsdSchemes)
            setValidationSchemeForFiles(basicFiles);
        return filesValidation(basicFiles);
    }


    private String filesValidation(List<BasicFile> basicFiles){
        String output = "";
        // proměnné a listy pouze pro debug a zobrazení statistiky ohledně validace.
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
                    System.err.println("There is no xsd file for validation: " + basicFile.getXsdSchemeName() + " FilePath " + basicFile.getPath());
                    if(!missingXsdFilesNames.contains(basicFile.getXsdSchemeName())){
                        missingXsdFilesNames.add(basicFile.getXsdSchemeName());
                    }
                    basicFile.setValidationEndType(ValidationEndType.MISSING_XSD_FILE);
                }
            } else {
                // missing xsd scheme in basicfile
                filesValidatedErrorNoXsdScheme.getAndIncrement();
                if(!missingXsdSchemeFilesPath.contains(basicFile.getPath())){
                    missingXsdSchemeFilesPath.add(basicFile.getPath());
                }
                basicFile.setValidationEndType(ValidationEndType.MISSING_XSD_SCHEME);
            }
            basicFile.setValidationDone(true);
        });
        System.out.println("Validated files : " + filesValidated);
        output += "\r\n" + "Počet validovaných souborů: " + filesValidated + "\r\n";
        System.out.println("Validated files Success : " + filesValidatedSuccess);
        output += "Počet úspěšně validovaných souborů: " + filesValidatedSuccess + "\r\n";
        System.out.println("Validated files error validation : " + filesValidatedErrorValidation);
        output += "Počet chybně validovaných souborů: " + filesValidatedErrorValidation + "\r\n";
        System.out.println("Validated files error no xsd file : " + filesValidatedErrorNoXsd);
        output += "Počet validovaných souborů, které neobsahují odkaz na xsd schéma: " + filesValidatedErrorNoXsd + "\r\n";
        System.out.println("Validated files error no xsd scheme : " + filesValidatedErrorNoXsdScheme);
        output += "Počet validovaných souborů, pro které není xsd schéma: " + filesValidatedErrorNoXsdScheme + "\r\n";
        System.out.println("Missing xsd files [" + missingXsdFilesNames.size() + "] " + missingXsdFilesNames);
        output += "Počet chybějících xsd souborů [" + missingXsdFilesNames.size() + "] Názvy xsd souborů: " + missingXsdFilesNames + "\r\n";
        System.out.println("File paths that missing xsd scheme [" + missingXsdSchemeFilesPath.size() + "] " + missingXsdSchemeFilesPath);
        output += "Počet souborů bez určených xsd schémat [" + missingXsdSchemeFilesPath.size() + "] Cesty k souborům bez xsd schémat: " + missingXsdSchemeFilesPath + "\r\n";
        return output;
    }

    public List<BasicFile> getValidatorsList() {
        return validatorsList;
    }

    public void setValidatorsDirectory(String validatorsDirectory) {
        this.validatorsDirectory = validatorsDirectory;
        loadValidators();
    }
}
