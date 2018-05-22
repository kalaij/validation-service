package uk.ac.ebi.subs.validator.schema;

public class JsonSchemaValidatorException extends RuntimeException {

    /**
     * Constructs a new JsonSchemaValidatorException exception with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public JsonSchemaValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JsonSchemaValidatorException exception with the specified detail message.
     *
     * @param message the detail message
     */
    public JsonSchemaValidatorException(String message) {
        super(message);
    }
}
