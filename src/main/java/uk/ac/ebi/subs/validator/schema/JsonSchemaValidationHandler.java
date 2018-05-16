package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonSchemaValidationHandler {

    private JsonSchemaValidationService validationService;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonSchemaValidationHandler(JsonSchemaValidationService validationService) {
        this.validationService = validationService;
    }

    public SingleValidationResultsEnvelope handleSampleValidation(SampleValidationMessageEnvelope envelope) {
        // TODO - handle logic on which schema to use for validation
        List<JsonSchemaValidationError> jsonSchemaValidationErrors;

        try {
            jsonSchemaValidationErrors = validationService.validate(
                    mapper.readTree("{}"), mapper.valueToTree(envelope.getEntityToValidate()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<SingleValidationResult> covertToSingleValidationResultList(List<JsonSchemaValidationError> errorList, String entityUuid) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        for (JsonSchemaValidationError error : errorList) {
            validationResults.add(generateToSingleValidationResult(error, entityUuid));
        }

        return validationResults;
    }

    private SingleValidationResult generateToSingleValidationResult(JsonSchemaValidationError error, String entityUuid) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(ValidationAuthor.JsonSchema);
        validationResult.setValidationStatus(SingleValidationResultStatus.Error);
        validationResult.setEntityUuid(entityUuid);
        validationResult.setMessage(error.getDataPath() + " error(s): " + error.getErrorsAsString());
        return validationResult;
    }

    private SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(ValidationMessageEnvelope envelope, List<SingleValidationResult> validationResults) {
        return new SingleValidationResultsEnvelope(
                validationResults,
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.JsonSchema
        );
    }
}
