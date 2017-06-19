package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;

@Service
public class StudyRefValidator {

    private final String MISSING_STUDY = "Could not find referenced study(ies): %s";
    private final String SUCCESS_MESSAGE = "All study references found.";

    @Autowired
    public StudyRepository repository;

    public SingleValidationResult validate(List<StudyRef> studyRefList, SingleValidationResult singleValidationResult) {

        StringBuilder accessions = new StringBuilder();

        for (StudyRef studyRef : studyRefList) {
            Study study = repository.findFirstByAccessionOrderByCreatedDateDesc(studyRef.getAccession());

            if (study == null) {
                if (accessions.toString().isEmpty()) {
                    accessions.append(studyRef.getAccession());
                } else {
                    accessions.append(", " + studyRef.getAccession());
                }
            }

            if (accessions.toString().isEmpty()) {
                singleValidationResult.setMessage(SUCCESS_MESSAGE);
                singleValidationResult.setValidationStatus(ValidationStatus.Pass);
            } else {
                singleValidationResult.setMessage(String.format(MISSING_STUDY, accessions));
                singleValidationResult.setValidationStatus(ValidationStatus.Error);
            }
        }

        return singleValidationResult;
    }
}
