package model.validation;

/**
 * Enum sloužící k definování výstupů validace souborů.
 * @see ValidationManager
 * @see Enum
 * @author Matěj Váňa
 * */
public enum ValidationEndType {
    SUCCESS, MISSING_XSD_FILE, VALIDATION_ERROR, MISSING_XSD_SCHEME, NOT_VALIDATED
}
