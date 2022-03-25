package model.files;

import model.validation.ValidationEndType;

import java.io.File;

public class BasicFile {

    private String path;
    private String name;
    private boolean zipped;

    private boolean validationDone;
    private ValidationEndType validationEndType;
    private String xsdSchemeName;

    public BasicFile(File file){
        this.path = file.getPath();
        this.name = file.getName();
        this.zipped = file.getPath().endsWith(".zip");
        validationDone = false;
        validationEndType = ValidationEndType.NOT_VALIDATED;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isZipped() {
        return zipped;
    }

    public void setZipped(boolean zipped) {
        this.zipped = zipped;
    }

    public boolean isXml(){
        return path.endsWith(".xml");
    }

    public boolean isXsd(){
        return path.endsWith(".xsd");
    }

    public String getXsdSchemeName() {
        return xsdSchemeName;
    }

    public void setXsdSchemeName(String xsdSchemeName) {
        this.xsdSchemeName = xsdSchemeName;
    }

    public boolean isValidationDone() {
        return validationDone;
    }

    public void setValidationDone(boolean validationDone) {
        this.validationDone = validationDone;
    }

    public ValidationEndType getValidationEndType() {
        return validationEndType;
    }

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
