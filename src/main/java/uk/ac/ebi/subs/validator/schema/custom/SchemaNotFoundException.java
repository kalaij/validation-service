package uk.ac.ebi.subs.validator.schema.custom;

public class SchemaNotFoundException extends JsonSchemaValidatorException {
    /**
     * Constructs a new SchemaNotFoundException exception with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public SchemaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SchemaNotFoundException exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SchemaNotFoundException(String message) {
        super(message);
    }
}
