package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
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

    public ValidationOutcome generateValidationOutcomeDocument(Sample sample, String submissionId) {
        logger.debug("Creating Validation Outcome Document for a Sample from submission {}", submissionId);

        String version = getVersion(submissionId, sample.getId());
        ValidationOutcome outcomeDocument = generateValidationOutcome(submissionId, sample, version);

        return outcomeDocument;
    }

    /**
     * ValidationOutcome versioning starts on 1.0 with increments of 0.1
     * @param submissionId
     * @param entityUuid
     * @return String version
     */
    public String getVersion(String submissionId, String entityUuid) {
        List<ValidationOutcome> validationOutcomes = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationOutcomes.size() > 0) {
            List<Double> doubleVersions = validationOutcomes.stream()
                    .map(validationOutcome -> Double.valueOf(validationOutcome.getVersion()))
                    .collect(Collectors.toList());

            double max = Collections.max(doubleVersions);
            return String.valueOf(max + 0.1);
        }
        return "1.0";
    }

    private ValidationOutcome generateValidationOutcome(String submissionId, Sample sample, String version) {
        ValidationOutcome outcomeDocument = new ValidationOutcome();
        outcomeDocument.setUuid(UUID.randomUUID().toString());
        outcomeDocument.setSubmissionId(submissionId);
        outcomeDocument.setEntityUuid(sample.getId());

        outcomeDocument.setVersion(version);

        Map<Archive, Boolean> expectedOutcomes = new HashMap<>();
        for (Archive archive : Arrays.asList(Archive.BioSamples, Archive.Ena, Archive.ArrayExpress)) {
            expectedOutcomes.put(archive, false);
        }
        outcomeDocument.setExpectedOutcomes(expectedOutcomes);
        return outcomeDocument;
    }

}
