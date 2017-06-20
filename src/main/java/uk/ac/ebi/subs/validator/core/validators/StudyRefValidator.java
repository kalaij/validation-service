package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

@Service
@EnableMongoRepositories(basePackageClasses = StudyRepository.class)
public class StudyRefValidator implements ReferenceValidator {

    @Autowired
    public StudyRepository studyRepository;

    /**
     * An Assay refers to a Study via a StudyRef.
     * @param studyRef
     * @param singleValidationResult
     */
    @Override
    public void validate(AbstractSubsRef studyRef, SingleValidationResult singleValidationResult) {
        Study study = studyRepository.findFirstByAccessionOrderByCreatedDateDesc(studyRef.getAccession());
        updateSingleValidationResult(study, studyRef, singleValidationResult);
    }

}
