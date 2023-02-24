package model.files;

import model.validation.ValidationEndType;

import java.io.File;

/**
 * Slouží k uchování informací o souboru, ukládá informace o cestě, jménu, typu xsd schématu, formátu, stavu validace,...
 * @see File
 * @author Matěj Váňa
 * */
public class BasicFile {

    private String path;
    private String name;
    private final boolean zipped;

    private boolean validationDone;
    private ValidationEndType validationEndType;
    private String xsdSchemeName;

    /**
     * Konstruktor
     * @param file Soubor, ze kterého se získají všechny informace
     * */
    public BasicFile(File file){
        this.path = file.getPath();
        this.name = file.getName();
        this.zipped = file.getPath().endsWith(".zip");
        validationDone = false;
        validationEndType = ValidationEndType.NOT_VALIDATED;
    }

    /**
     * Získání cesty k souboru
     * @return String s cestou k souboru
     * */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Získání jména souboru
     * @return String s jménem souboru
     * */
    public String getName() {
        return name;
    }

    /**
     * Nastavení názvu souboru
     * @param name název souboru
     * */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Získání informace zda je soubor v zip
     * @return boolean zda je soubor zabalen v zip
     * */
    public boolean isZipped() {
        return zipped;
    }

    /**
     * Získání informace zda je soubor ve formátu xml
     * @return boolean zda je soubor ve formátu xml
     * */
    public boolean isXml(){
        return path.endsWith(".xml");
    }

    /**
     * Získání informace zda je soubor ve formátu xsd
     * @return boolean zda je soubor ve formátu xsd
     * */
    public boolean isXsd(){
        return path.endsWith(".xsd");
    }

    /**
     * Získání názvu xsd souboru, který je potřeba k validaci
     * @return String s názvem xsd souboru
     * */
    public String getXsdSchemeName() {
        return xsdSchemeName;
    }

    /**
     * Nastavení názvu xsd souboru, který je potřeba k validaci
     * @param xsdSchemeName název xsd souboru potřebného k validaci
     * */
    public void setXsdSchemeName(String xsdSchemeName) {
        this.xsdSchemeName = xsdSchemeName;
    }

    /**
     * Získání informace zda je soubor v zip
     * @return boolean zda je soubor zabalen v zip
     * */
    public boolean isValidationDone() {
        return validationDone;
    }

    /**
     * Nastavení zda je hotová validace
     * @param validationDone boolean s informací zda je hotová validace
     * */
    public void setValidationDone(boolean validationDone) {
        this.validationDone = validationDone;
    }

    /**
     * Vrácení typu, se kterým skončila validace souboru viz {@link ValidationEndType}
     * @see ValidationEndType
     * @see model.validation.ValidationManager
     * @return ValidationEndType s výsledkem validace
     * */
    public ValidationEndType getValidationEndType() {
        return validationEndType;
    }

    /**
     * Nastavení {@link ValidationEndType} jako výsledku validace.
     * @see ValidationEndType
     * @see model.validation.ValidationManager
     * @param validationEndType nastavení výsledku validace
     * */
    public void setValidationEndType(ValidationEndType validationEndType) {
        this.validationEndType = validationEndType;
    }

    @Override
    public String toString() {
        return "BasicFile{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", zipped=" + zipped +
                ", validationDone=" + validationDone +
                ", validationEndType=" + validationEndType +
                ", xsdSchemeName='" + xsdSchemeName + '\'' +
                '}';
    }
}
