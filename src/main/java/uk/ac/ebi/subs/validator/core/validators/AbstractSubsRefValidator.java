package uk.ac.ebi.subs.validator.core.validators;

import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.UUID;

public class AbstractSubsRefValidator {

    public SingleValidationResult validate(SampleRelationship sampleRelationship, String entityId) {
        return null;
    }

    public SingleValidationResult validate(StudyRef studyRef, String entityId) {
        return null;
    }

    public SingleValidationResult validate(AssayRef assayRef, String entityId) {
        return null;
    }

    private SingleValidationResult generateSingleValidationResult(String entityId, String message, ValidationStatus status) {
        SingleValidationResult result = new SingleValidationResult();
        result.setUuid(UUID.randomUUID().toString());
        result.setEntityUuid(entityId);
        result.setMessage(message);
        result.setValidationAuthor(ValidationAuthor.Core);
        result.setValidationStatus(status);
        return result;
    }

}
