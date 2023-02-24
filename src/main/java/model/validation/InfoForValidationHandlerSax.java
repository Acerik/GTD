package model.validation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Třída slouží ke čtení xml souborů a nalezení názvu xsd schématu, které daný soubor potřebuje k validaci
 * @author Matěj Váňa
 * */
public class InfoForValidationHandlerSax extends DefaultHandler {

    private String validationXsdName = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        /*
        * Hledání názvu xsd souboru pro validaci, za správný soubor k validaci se považuje první nalezený xsd soubor
         */
        if(attributes.getValue("xsi:noNamespaceSchemaLocation") != null && validationXsdName == null) {
            validationXsdName = attributes.getValue("xsi:noNamespaceSchemaLocation");
        } else if(attributes.getValue("xsi:schemaLocation") != null && validationXsdName == null){
            if (attributes.getValue("xsi:schemaLocation").contains(" ")){
                String[] tempAttribute = attributes.getValue("xsi:schemaLocation").split(" ");
                for (String s : tempAttribute) {
                    if (s.endsWith(".xsd")) {
                        validationXsdName = s.trim();
                        break;
                    }
                }
            }
        } else if(attributes.getValue("xmlns") != null && validationXsdName == null){
            if(attributes.getValue("xmlns").split(":").length > 1) {
                String[] xsdArrayParam = attributes.getValue("xmlns").split(":");
                //urn   iec62325.351   tc57wg16    451-3    totalallocationresultdocument   7   0
                if(xsdArrayParam[1].contains("."))
                    xsdArrayParam[1] = xsdArrayParam[1].split("\\.")[0];

                validationXsdName = xsdArrayParam[1] + "-"
                        + xsdArrayParam[3] + "-"
                        + xsdArrayParam[4].replace("document", "").replace("result", "") + "_"
                        + "v" + xsdArrayParam[5] + "_" + xsdArrayParam[6] + ".xsd";
            } else{
                validationXsdName = attributes.getValue("xmlns");
            }
        }
        super.startElement(uri, localName, qName, attributes);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    /**
     * Vrací nalezený název xsd souboru, který je potřeba k validaci
     * @return String s názvem xsd souborů, který je potřeba k validaci souboru
     * @see model.files.BasicFile
     * */
    public String getValidationXsdName() {
        return validationXsdName;
    }
}
