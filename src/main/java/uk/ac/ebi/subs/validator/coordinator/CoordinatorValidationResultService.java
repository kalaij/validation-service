package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.data.structures.GlobalValidationStatus;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;
import uk.ac.ebi.subs.validator.util.BlankValidationResultMaps;

@Service
public class CoordinatorValidationResultService {
    private static Logger logger = LoggerFactory.getLogger(CoordinatorValidationResultService.class);

    @Autowired
    private ValidationResultRepository repository;

    public ValidationResult generateValidationResultDocument(Sample sample){
        ValidationResult validationResult = findAndUpdateValidationResult(sample);
        validationResult.setExpectedResults(BlankValidationResultMaps.forSample());

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(Study study){
        ValidationResult validationResult = findAndUpdateValidationResult(study);
        validationResult.setExpectedResults(BlankValidationResultMaps.forStudy());

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(Assay assay){
        ValidationResult validationResult = findAndUpdateValidationResult(assay);
        validationResult.setExpectedResults(BlankValidationResultMaps.forAssay());

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(AssayData assayData){
        ValidationResult validationResult = findAndUpdateValidationResult(assayData);
        validationResult.setExpectedResults(BlankValidationResultMaps.forAssayData());

        repository.save(validationResult);

        return validationResult;
    }

    private ValidationResult findAndUpdateValidationResult(Submittable submittable) {
        String submittableUuid = submittable.getId();
        ValidationResult validationResult = repository.findByEntityUuid(submittableUuid);
        if (validationResult != null) {
            validationResult.setValidationStatus(GlobalValidationStatus.Pending);
            validationResult.setVersion(validationResult.getVersion() + 1);
            logger.debug("ValidationResult has been changed to status: {} and version: {}",
                    validationResult.getValidationStatus().name(), validationResult.getVersion());
        } else {
            throw new IllegalStateException(String.format("Could not find ValidationResult for submittable with ID: %s", submittable.getId()));
        }

        return validationResult;
    }
}
