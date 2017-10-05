package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.repository.model.Study;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StudyTypeValidatorTest {

    private StudyTypeValidator studyTypeValidator = new StudyTypeValidator();
    private Study study;

    @Before
    public void buildUp() {
        study = new Study();
        study.setId("12345");
    }

    @Test
    public void studyTypePresentSoPass() {
        study.setStudyType(StudyDataType.Metabolomics);

        SingleValidationResult singleValidationResult = studyTypeValidator.validate(study);

        assertEquals(SingleValidationResultStatus.Pass, singleValidationResult.getValidationStatus());
        assertNull(singleValidationResult.getMessage());
    }

    @Test
    public void studyTypeAbsentSoFail() {
        study.setStudyType(null);

        SingleValidationResult singleValidationResult = studyTypeValidator.validate(study);

        assertEquals(SingleValidationResultStatus.Error, singleValidationResult.getValidationStatus());
        assertNotNull(singleValidationResult.getMessage());
    }

}
