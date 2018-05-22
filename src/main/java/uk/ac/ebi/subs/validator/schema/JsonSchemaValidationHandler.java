package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.ac.ebi.subs.validator.util.ValidationHelper.generatePassingSingleValidationResult;
import static uk.ac.ebi.subs.validator.util.ValidationHelper.generateSingleValidationResultsEnvelope;

@Service
public class JsonSchemaValidationHandler {

    private JsonSchemaValidationService validationService;
    private SchemaService schemaService;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonSchemaValidationHandler(JsonSchemaValidationService validationService, SchemaService schemaService) {
        this.validationService = validationService;
        this.schemaService = schemaService;
    }

    public SingleValidationResultsEnvelope handleSampleValidation(SampleValidationMessageEnvelope envelope) {
        JsonNode sampleSchema = schemaService.getSchemaFor(envelope.getEntityToValidate()); // TODO - handle logic on which schema to use for validation

        List<JsonSchemaValidationError> jsonSchemaValidationErrors = validationService.validate(sampleSchema, mapper.valueToTree(envelope.getEntityToValidate()));
        List<SingleValidationResult> singleValidationResultList = getSingleValidationResults(envelope, jsonSchemaValidationErrors);

        return generateSingleValidationResultsEnvelope(envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(), singleValidationResultList, ValidationAuthor.JsonSchema);
    }

    public SingleValidationResultsEnvelope handleStudyValidation(StudyValidationMessageEnvelope envelope)  {
        JsonNode studySchema = schemaService.getSchemaFor(envelope.getEntityToValidate()); // TODO - handle logic on which schema to use for validation

        List<JsonSchemaValidationError> jsonSchemaValidationErrors = validationService.validate(studySchema, mapper.valueToTree(envelope.getEntityToValidate()));
        List<SingleValidationResult> singleValidationResultList = getSingleValidationResults(envelope, jsonSchemaValidationErrors);

        return generateSingleValidationResultsEnvelope(envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(), singleValidationResultList, ValidationAuthor.JsonSchema);
    }

    // -- Helper methods -- //
    private List<SingleValidationResult> getSingleValidationResults(ValidationMessageEnvelope envelope, List<JsonSchemaValidationError> jsonSchemaValidationErrors) {
        List<SingleValidationResult> singleValidationResultList;
        if(jsonSchemaValidationErrors.isEmpty()) {
            singleValidationResultList = Arrays.asList(generatePassingSingleValidationResult(envelope.getEntityToValidate().getId(), ValidationAuthor.JsonSchema));
        } else {
            singleValidationResultList = covertToSingleValidationResultList(jsonSchemaValidationErrors, envelope.getEntityToValidate().getId());
        }
        return singleValidationResultList;
    }

    private List<SingleValidationResult> covertToSingleValidationResultList(List<JsonSchemaValidationError> errorList, String entityUuid) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        for (JsonSchemaValidationError error : errorList) {
            validationResults.add(generateSchemaSingleValidationResult(error, entityUuid));
        }
        return validationResults;
    }

    private SingleValidationResult generateSchemaSingleValidationResult(JsonSchemaValidationError error, String entityUuid) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(ValidationAuthor.JsonSchema);
        validationResult.setValidationStatus(SingleValidationResultStatus.Error);
        validationResult.setEntityUuid(entityUuid);
        validationResult.setMessage(error.getDataPath() + " error(s): " + error.getErrorsAsString());
        return validationResult;
    }

}
