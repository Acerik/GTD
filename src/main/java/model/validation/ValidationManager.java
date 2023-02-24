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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Třída sloužící ke správě validací
 * Umožňujě načtení validátorů, jejich uložení v paměti, nalezení xsd schémat v souborech a jejich přiřazení k souborům, validaci zvolených souborů
 * @author Matěj Váňa
 * */
public class ValidationManager {

    /**
     * Cesta ke složce s validátory.
     * */
    private String validatorsDirectory;

    /**
     * Slouží k uložení validátorů do listu s {@link BasicFile}
     * @see BasicFile
     * @see List
     * */
    private List<BasicFile> validatorsList;

    public ValidationManager(){ }


    /**
     * Pomocí {@link FileHandler} načte validátory ze zadané složky a uloží je do listu validátorů
     * @see FileHandler
     * */
    private void loadValidators(){
        validatorsList = FileHandler.loadFilesWithPath(validatorsDirectory);
    }

    /**
     * Nachystá xsd schémata pro vstupní data, aby mohlo dojít k jejich validaci
     * @param basicFiles List základních souborů dat, ve kterých je potřeba najít xsd schémata
     */
    public void setValidationSchemeForFiles(List<BasicFile> basicFiles) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        basicFiles.forEach(basicFile -> {
            if(basicFile.isXml()) {
                try {
                    SAXParser saxParser = factory.newSAXParser();
                    InfoForValidationHandlerSax handlerSax = new InfoForValidationHandlerSax();
                    saxParser.parse(basicFile.getPath(), handlerSax);
                    String xsdSchemeName = handlerSax.getValidationXsdName();
                    if(xsdSchemeName == null)
                        xsdSchemeName = "";
                    basicFile.setXsdSchemeName(xsdSchemeName);
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Slouží k validaci xml souborů
     * @param basicFiles List {@link BasicFile}, které se mají validatovat na základě předem nalezlých xsd schémat.
     * @param findXsdSchemes {@code true} zda se mají pro zadané soubory první vyhledat xsd schémata, {@code false} pokud ne
     * @return String s možnými chybami, případně výstupem s informacemi vhodnými do konzole.
     */
    public String validateFiles(List<BasicFile> basicFiles, boolean findXsdSchemes){
        if(findXsdSchemes)
            setValidationSchemeForFiles(basicFiles);
        return filesValidation(basicFiles);
    }


    /**
     * Samotná validace xsd souborů.
     * @param basicFiles List {@link BasicFile} se soubory určenými k validaci
     * @return String s výpisem do konzole.
     * @see List
     * @see BasicFile
     * @see InfoForValidationHandlerSax
     * */
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

        basicFiles.stream().filter(BasicFile::isXml).forEach(basicFile -> {
            filesValidated.getAndIncrement();
            if(!Objects.equals(basicFile.getXsdSchemeName(), "")){
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
        System.out.println("Validated files Success : " + filesValidatedSuccess);
        System.out.println("Validated files error validation : " + filesValidatedErrorValidation);
        System.out.println("Validated files error no xsd file : " + filesValidatedErrorNoXsd);
        System.out.println("Validated files error no xsd scheme : " + filesValidatedErrorNoXsdScheme);
        System.out.println("Missing xsd files [" + missingXsdFilesNames.size() + "] " + missingXsdFilesNames);
        System.out.println("File paths that missing xsd scheme [" + missingXsdSchemeFilesPath.size() + "] " + missingXsdSchemeFilesPath);

        output += "\r\n" + "Počet validovaných souborů: " + filesValidated + "\r\n";
        output += "Počet úspěšně validovaných souborů: " + filesValidatedSuccess + "\r\n";
        output += "Počet chybně validovaných souborů: " + filesValidatedErrorValidation + "\r\n";
        output += "Počet validovaných souborů, které neobsahují odkaz na xsd schéma: " + filesValidatedErrorNoXsd + "\r\n";
        output += "Počet validovaných souborů, pro které není xsd schéma: " + filesValidatedErrorNoXsdScheme + "\r\n";
        output += "Počet chybějících xsd souborů [" + missingXsdFilesNames.size() + "] Názvy xsd souborů: " + missingXsdFilesNames + "\r\n";
        output += "Počet souborů bez určených xsd schémat [" + missingXsdSchemeFilesPath.size() + "] Cesty k souborům bez xsd schémat: " + missingXsdSchemeFilesPath + "\r\n";
        return output;
    }

    /**
     * Vrací list validátorů.
     * @return List {@link BasicFile} s validátory.
     * @see BasicFile
     * @see List
     * */
    public List<BasicFile> getValidatorsList() {
        return validatorsList;
    }

    /**
     * Slouží k nastavení cesty pro validátory. Zároveň dojde k načtení validátorů do seznamu
     * @param validatorsDirectory String s cestou na složku s validátory
     * */
    public void setValidatorsDirectory(String validatorsDirectory) {
        this.validatorsDirectory = validatorsDirectory;
        loadValidators();
    }
}
