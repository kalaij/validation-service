package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;

@Service
public class SampleRelationshipValidator {

    private final String MISSING_SAMPLE = "Could not find referenced sample(s): %s";
    private final String SUCCESS_MESSAGE = "All sample references found.";

    @Autowired
    public SampleRepository repository;

    public SingleValidationResult validate(List<SampleRelationship> sampleRelationshipList, SingleValidationResult singleValidationResult) {

        StringBuilder accessions = new StringBuilder();

        for (SampleRelationship sampleRelationship : sampleRelationshipList) {
                Sample sample = repository.findByAccession(sampleRelationship.getAccession());

            if (sample == null) {
                if(accessions.toString().isEmpty()) {
                    accessions.append(sampleRelationship.getAccession());
                } else {
                    accessions.append(", " + sampleRelationship.getAccession());
                }
            }

            if (accessions.toString().isEmpty()) {
                singleValidationResult.setMessage(SUCCESS_MESSAGE);
                singleValidationResult.setValidationStatus(ValidationStatus.Pass);
            } else {
                singleValidationResult.setMessage(String.format(MISSING_SAMPLE, accessions));
                singleValidationResult.setValidationStatus(ValidationStatus.Error);
            }
        }

        return singleValidationResult;
    }

}
