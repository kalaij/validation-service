package uk.ac.ebi.subs.validator.messaging;

/**
 * This class holds the validation routing keys for the core-validator.
 *
 */
public class FileReferenceRoutingKeys {

    public static final String EVENT_VALIDATION_SUCCESS = "validation.success";

    public static final String EVENT_VALIDATION_ERROR = "validation.error";

    public static final String EVENT_FILE_REFERENCE_VALIDATION = "file.filereference.validation";

    public static final String EVENT_ASSAYDATA_FILEREF_VALIDATION = "file.assaydata.filereference.validation";
}
