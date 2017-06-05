package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OutcomeDocumentService {
    private static Logger logger = LoggerFactory.getLogger(OutcomeDocumentService.class);

    @Autowired
    private ValidationOutcomeRepository repository;

    public ValidationOutcome generateValidationOutcomeDocument(BaseSubmittable submittable, String submissionId) {
        logger.debug("Creating Validation Outcome Document for {} from submission {}",
                submittable.getClass().getSimpleName(), submissionId);

        int version = getVersion(submissionId, submittable.getId());
        ValidationOutcome outcomeDocument = generateValidationOutcome(submissionId, submittable, version);

        return outcomeDocument;
    }

    /**
     * ValidationOutcome versioning starts on 1 with increments of 1.
     * @param submissionId
     * @param entityUuid
     * @return String version
     */
    public int getVersion(String submissionId, String entityUuid) {
        List<ValidationOutcome> validationOutcomes = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationOutcomes.size() > 0) {
            List<Integer> versions = validationOutcomes.stream()
                    .map(validationOutcome -> Integer.valueOf(validationOutcome.getVersion()))
                    .collect(Collectors.toList());

            int max = Collections.max(versions);
            int version = max + 1;

            deleteObsoleteValidationOutcomeResults(validationOutcomes);

            return version;
        }
        return 1;
    }

    private ValidationOutcome generateValidationOutcome(String submissionId, BaseSubmittable submittable, int version) {
        ValidationOutcome outcomeDocument = new ValidationOutcome();
        outcomeDocument.setUuid(UUID.randomUUID().toString());
        outcomeDocument.setSubmissionId(submissionId);
        outcomeDocument.setEntityUuid(submittable.getId());

        outcomeDocument.setVersion(version);

        Map<Archive, Boolean> expectedOutcomes = new HashMap<>();
        for (Archive archive : Arrays.asList(Archive.BioSamples, Archive.Ena, Archive.ArrayExpress)) {
            expectedOutcomes.put(archive, false);
        }
        outcomeDocument.setExpectedOutcomes(expectedOutcomes);
        return outcomeDocument;
    }

    private void deleteObsoleteValidationOutcomeResults(List<ValidationOutcome> validationOutcomes) {
        repository.delete(validationOutcomes);
    }
}
