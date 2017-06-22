package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

@Service
@EnableMongoRepositories(basePackageClasses = AssayRepository.class)
public class AssayRefValidator implements ReferenceValidator {

    @Autowired
    public AssayRepository assayRepository;

    /**
     * An AssayData refers to an Assay via an AssayRef
     * @param assayRef
     * @param singleValidationResult
     */
    @Override
    public void validate(AbstractSubsRef assayRef, SingleValidationResult singleValidationResult) {
        Assay assay;

        if (assayRef.getAccession() != null && !assayRef.getAccession().isEmpty()) {
            assay = assayRepository.findFirstByAccessionOrderByCreatedDateDesc(assayRef.getAccession());
        } else {
            assay = assayRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(assayRef.getTeam(), assayRef.getAlias());
        }

        if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
            initializeSingleValidationResult(assay, assayRef, singleValidationResult);
        } else {
            updateSingleValidationResult(assay, assayRef, singleValidationResult);
        }
    }

}
