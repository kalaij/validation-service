package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import static uk.ac.ebi.subs.validator.core.validators.ValidatorHelper.getDefaultSingleValidationResult;

@Service
public class StudyTypeValidator {

    private static final String FAIL_MESSAGE = "Study type is required.";

    /**
     * A study describes the type of data it includes with the study type field
     *
     * @param study
     * @return singleValidationResult
     */
    public SingleValidationResult validate(Study study) {
        SingleValidationResult singleValidationResult = ValidatorHelper.getDefaultSingleValidationResult(study.getId());
        StudyDataType studyDataType = study.getStudyType();

        if (studyDataType == null) {
            singleValidationResult.setMessage(FAIL_MESSAGE);
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }
        else {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        }


        return singleValidationResult;
    }
}
