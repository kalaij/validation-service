package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

@Service
public class StudyTypeValidator {

    private static final String FAIL_MESSAGE = "Study type is required";

    /**
     * A study describes the type of data it includes with the study type field
     *
     * @param study
     * @param singleValidationResult
     */
    public void validate(Study study, SingleValidationResult singleValidationResult) {
        StudyDataType studyDataType = study.getStudyType();

        if (studyDataType == null) {
            singleValidationResult.setMessage(FAIL_MESSAGE);
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }


    }
}
